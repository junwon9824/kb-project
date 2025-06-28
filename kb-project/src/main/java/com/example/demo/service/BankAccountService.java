package com.example.demo.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.BankAccountDto;
import com.example.demo.dto.TransferDto;
import com.example.demo.entity.Bank;
import com.example.demo.entity.BankAccount;
import com.example.demo.entity.BookMark;
import com.example.demo.entity.Log;
import com.example.demo.entity.User;
import com.example.demo.repository.BankAccountRepository;
import com.example.demo.repository.LogRepository;
import com.example.demo.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class BankAccountService {

    private static final Logger logger = LoggerFactory.getLogger(BankAccountService.class);
    // 예시: 클래스 상단에 한도 상수 선언
    private static final long MIN_DEPOSIT_AMOUNT = 1000L;      // 최소 1,000원
    private static final long MAX_DEPOSIT_AMOUNT = 10000000L;  // 최대 1,000만 원

    private final BankAccountRepository bankAccountRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final BankService bankService;
    private final LogService logService;
    private final LogRepository logRepository;

    public BankAccountService(BankAccountRepository bankAccountRepository, UserRepository userRepository,
                              UserService userService, BankService bankService, LogRepository logRepository, LogService logService) {
        this.bankAccountRepository = bankAccountRepository;
        this.userRepository = userRepository;
        this.userService = userService;
        this.bankService = bankService;
        this.logRepository = logRepository;
        this.logService = logService;
    }

    @Transactional
    public void depositToAccount(String accountNumber, Long amount, String depositorName) {
        //현금 입금도 가능..
        try {
            logger.info("입금 요청: 계좌={}, 금액={}, 입금자={}", accountNumber, amount, depositorName);

            // 1. 계좌를 비관적 락으로 조회
            BankAccount account = Optional
                    .ofNullable(bankAccountRepository.findByAccountNumberWithLock(accountNumber))
                    .orElseThrow(() -> new IllegalArgumentException("입금 계좌 없음"));


            // 2. 입금 금액 검증 (최소/최대 한도 포함)
            if (amount == null || amount < MIN_DEPOSIT_AMOUNT || amount > MAX_DEPOSIT_AMOUNT) {
                logger.warn("잘못된 입금 금액: {}", amount);
                throw new IllegalArgumentException("입금 금액은 " + MIN_DEPOSIT_AMOUNT + "원 이상, " + MAX_DEPOSIT_AMOUNT + "원 이하이어야 합니다.");
            }


            Long originalAmount = account.getAmount();
            try {
                // 3. 잔액 업데이트
                account.setAmount(originalAmount + amount);
                updateBankAccount(account.toDto());

                // 4. 입금 내역 기록 (Log 엔티티 활용)
                Log depositLog = Log.builder()
                        .amount(amount)
                        .category("입금")
                        .recipientBankNumber(account.getAccountNumber())
                        .recipientName(account.getUser() != null ? account.getUser().getUsername() : "알수없음")
                        .senderName(depositorName)
                        .recipientAccount(account)
                        .build();

                logService.depositSave(depositLog);

                logger.info("입금 성공: 계좌={}, 금액={}", accountNumber, amount);

            } catch (Exception e) {
                account.setAmount(originalAmount); // 복구
                logger.error("입금 도중 예외 발생: {}, 복구 수행", e.getMessage(), e);
                throw e;
            }
        } catch (PessimisticLockingFailureException e) {
            logger.error("락 획득 실패: {}", e.getMessage(), e);
            throw e;
        } catch (DataAccessException e) {
            logger.error("DB 접근 실패: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            logger.error("입금 처리 중 알 수 없는 예외: {}", e.getMessage(), e);
            throw e;
        }
    }


    public BankAccount updateBankAccount(BankAccountDto dto) {
        return bankAccountRepository.findById(dto.getId()).map(account -> {
            account.setAccountNumber(dto.getAccountNumber());
            account.setAmount(dto.getAmount());
            account.setBank(dto.getBank());
            account.setUser(dto.getUser());
            return bankAccountRepository.save(account);
        }).orElse(null);
    }

    public List<BankAccount> getBankAccountByUser(User user) {
        return bankAccountRepository.findAll().stream()
                .filter(acc -> acc.getUser().getUserid().equals(user.getUserid()))
                .collect(Collectors.toList());
    }

    public BankAccount getBankAccountById(Long id) {
        return bankAccountRepository.findById(id).orElse(null);
    }

    public BankAccount getBankAccountByAccountnumber(String accountNumber) {
        return bankAccountRepository.findByAccountNumber(accountNumber);
    }

    public BankAccount createBankAccount(BankAccount account, Bank bank, User user) {
        // 중복 계좌 검증
        BankAccount existingAccount = bankAccountRepository.findByAccountNumber(account.getAccountNumber());
        if (existingAccount != null) {
            throw new IllegalArgumentException("동일한 계좌번호가 이미 존재합니다: " + account.getAccountNumber());
        }
        
        account.setUser(user);
        account.setBank(bank);
        return bankAccountRepository.save(account);
    }

    public BankAccount deleteBankAccount(BankAccount bankAccount) {
        bankAccountRepository.deleteByAccountNumber(bankAccount.getAccountNumber());
        return bankAccount;
    }

    public void transferToBookMarkUser(BookMark bookMark, User sender, Long amount) {
        TransferDto dto = TransferDto.builder()
                .amount(amount)
                .category("transfer")
                .recipient_banknumber(bookMark.getBookMarkAccountNumber())
                .recipient_name(bookMark.getBookMarkName())
                .sender_banknumber(sender.getBankAccounts().get(0).getAccountNumber())
                .sender_name(sender.getUsername())
                .build();

        transferToUser(dto, sender);
    }

    @Transactional
    public void transferToUser(TransferDto dto, User sender) {
        try {
            log.info("after ttttry " + dto.toString());
            BankAccount senderAcc = Optional
                    .ofNullable(bankAccountRepository.findByAccountNumberWithLock(dto.getSender_banknumber()))
                    .orElseThrow(() -> new IllegalArgumentException("송금자 계좌 없음"));
            BankAccount recipientAcc = Optional
                    .ofNullable(bankAccountRepository.findByAccountNumberWithLock(dto.getRecipient_banknumber()))
                    .orElseThrow(() -> new IllegalArgumentException("수신자 계좌 없음"));

            Long sendAmount = dto.getAmount();

            if (senderAcc.getAmount() < sendAmount) {
                logger.warn("잔액 부족: {}", senderAcc.getAccountNumber());
                return;
            }

            Long originalSenderAmount = senderAcc.getAmount();
            try {
                // 잔액 차감 및 수신자 금액 증가
                senderAcc.setAmount(originalSenderAmount - sendAmount);
                log.info("after senderAcccccc");

                User recipientUser = userService.getUserByUsernameAndBankAccount(dto.getRecipient_name(),
                        recipientAcc.getAccountNumber());

                recipientAcc.setAmount(recipientAcc.getAmount() + sendAmount);
                logger.info("after recipientAcc");

                // 수동으로 BankAccountDto 생성
                BankAccountDto senderDto = new BankAccountDto();
                senderDto.setId(senderAcc.getId());
                senderDto.setAccountNumber(senderAcc.getAccountNumber());
                senderDto.setAmount(senderAcc.getAmount());
                senderDto.setBank(senderAcc.getBank());
                senderDto.setUser(senderAcc.getUser());
                senderDto.setMainAccount(senderAcc.isMainAccount());

                BankAccountDto recipientDto = new BankAccountDto();
                recipientDto.setId(recipientAcc.getId());
                recipientDto.setAccountNumber(recipientAcc.getAccountNumber());
                recipientDto.setAmount(recipientAcc.getAmount());
                recipientDto.setBank(recipientAcc.getBank());
                recipientDto.setUser(recipientAcc.getUser());
                recipientDto.setMainAccount(recipientAcc.isMainAccount());

                updateBankAccount(senderAcc.toDto());
                updateBankAccount(recipientAcc.toDto());

                logger.info("before sender logggg");
                logger.info("dto.tostring()))))" + dto.toString());

                Log senderLog = dto.toEntity(); // 송금 로그
                logger.info("before recipientLog logggg" + recipientAcc.toString());
                logger.info("after senderLog logggg" + senderLog.toString());

                Log recipientLog = TransferDto.builder()
                        .amount(dto.getAmount())
                        .category("입금")
                        .recipient_banknumber(dto.getRecipient_banknumber())
                        .sender_banknumber(dto.getSender_banknumber())
                        .sender_name(dto.getSender_name())
                        .recipient_name(dto.getRecipient_name())
                        .recipientAccount(recipientAcc)
                        .senderUserId(sender.getUserid())
                        .recipientUserId(recipientUser.getUserid())
                        .senderAccount(senderAcc)
                        .build()
                        .toEntity(); // 입금 로그
                logger.info("before logService ");

                senderLog.setCategory("송금");
                logger.info("senderLogsenderLog" + senderLog.toString());
                logger.info("recipientLogrecipientLog" + recipientLog.toString());

                logService.transfersave(senderLog);
                logService.transfersave(recipientLog);

            } catch (Exception e) {
                senderAcc.setAmount(originalSenderAmount); // 복구
                logger.error("송금 도중 예외 발생: {}, 복구 수행", e.getMessage(), e);
                throw e;
            }
        } catch (PessimisticLockingFailureException e) {
            logger.error("락 획득 실패: {}", e.getMessage(), e);
        } catch (DataAccessException e) {
            logger.error("DB 접근 실패: {}", e.getMessage(), e);
        } catch (Exception e) {
            logger.error("알 수 없는 예외: {}", e.getMessage(), e);
        }
    }

    public void setMainAccount(BankAccountDto dto) {
        List<BankAccount> allAccounts = bankAccountRepository.findAll();

        allAccounts.stream()
                .filter(BankAccount::isMainAccount)
                .forEach(acc -> {
                    acc.setMainAccount(false);
                    bankAccountRepository.save(acc);
                });

        bankAccountRepository.findById(dto.getId()).ifPresent(account -> {
            account.setMainAccount(true);
            bankAccountRepository.save(account);
        });
    }

    public void setmainAccount(BankAccountDto dto) {
        List<BankAccount> allAccounts = bankAccountRepository.findAll();

        allAccounts.stream()
                .filter(BankAccount::isMainAccount)
                .forEach(acc -> {
                    acc.setMainAccount(false);
                    bankAccountRepository.save(acc);
                });

        bankAccountRepository.findById(dto.getId()).ifPresent(account -> {
            account.setMainAccount(true);
            bankAccountRepository.save(account);
        });
    }

    public List<BankAccount> getBankAccountByuserId(User user) {
        return getBankAccountByUser(user);
    }

    public BankAccount getBankAccountByAccountId(Long id) {
        return bankAccountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("BankAccount not found with id: " + id));
    }
}
