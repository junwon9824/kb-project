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
				log.info("after recipientAcc");

				updateBankAccount(senderAcc.toDto());
				updateBankAccount(recipientAcc.toDto());

				log.info("before sender logggg");
				log.info("dto.tostring()))))" + dto.toString());

				Log senderLog = dto.toEntity(); // 송금 로그
				log.info("before recipientLog logggg" + recipientAcc.toString());
				log.info("after senderLog logggg" + senderLog.toString());

				Log recipientLog = TransferDto.builder()
						.amount(dto.getAmount())
						.category("입금")
						.recipient_banknumber(dto.getRecipient_banknumber())
						.sender_banknumber(dto.getSender_banknumber())
						.sender_name(dto.getSender_name())
						.recipient_name(dto.getRecipient_name())
						.recipientAccount(recipientAcc)
						.senderAccount(senderAcc)
						.build()
						.toEntity(); // 입금 로그
				log.info("before logService ");

				senderLog.setCategory("송금");
				log.info("senderLogsenderLog" + senderLog.toString());
				log.info("recipientLogrecipientLog" + recipientLog.toString());

				logService.save(senderLog);
				logService.save(recipientLog);

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
