package com.example.demo.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.BankAccountDto;
import com.example.demo.dto.TransferDto;
import com.example.demo.entity.*;
import com.example.demo.repository.BankAccountRepository;
import com.example.demo.repository.LogRepository;
import com.example.demo.repository.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(rollbackFor = Exception.class)
public class BankAccountService {

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

	public BankAccount updateBankAccount(BankAccountDto bankAccount) {
		// 계좌 조회

		BankAccount existingAccount = bankAccountRepository.findById(bankAccount.getId()).orElse(null);

		if (existingAccount != null) {
			// 기존 계좌 정보 업데이트
			existingAccount.setAccountNumber(bankAccount.getAccountNumber());
			existingAccount.setAmount(bankAccount.getAmount());
			existingAccount.setBank(bankAccount.getBank());
			existingAccount.setUser(bankAccount.getUser());

			// 변경사항 저장
			return bankAccountRepository.save(existingAccount);
		}

		return null; // 계좌가 존재하지 않을 경우 null 반환
	}

	public List<BankAccount> getBankAccountByUser(User user) {

		String userid = user.getUserid();
		System.out.println(userid);
		List<BankAccount> bankAccounts = bankAccountRepository.findAll();
		List<BankAccount> bankAccounts2 = new ArrayList<BankAccount>();

		if (bankAccounts.isEmpty()) {
			return null;
		} else {

			for (BankAccount bankAccount : bankAccounts) {
				if (bankAccount.getUser().getUserid().equals(userid)) {
					bankAccounts2.add(bankAccount);
				}
			}

			return bankAccounts2;
		}
	}

	public BankAccount getBankAccountByAccountId(Long id) {

		Optional<BankAccount> bankAccount = bankAccountRepository.findById(id);
		return bankAccount.get();

	}

	public BankAccount getBankAccountByAccountnumber(String accountnumber) {

		System.out.println("acc");
		return bankAccountRepository.findByAccountNumber(accountnumber);

	}

	public BankAccount createBankAccount(BankAccount bankAccount, Bank bank, User user) {
		bankAccount.setUser(user);
		bankAccount.setBank(bank);
		return bankAccountRepository.save(bankAccount);
	}

	@Transactional
	public BankAccount deleteBankAccount(BankAccount bankAccount) {
		String accountnumber = bankAccount.getAccountNumber();
		bankAccountRepository.deleteByAccountNumber(accountnumber);

		return bankAccount;
	}

	// BookMark User에게 송금
	// 음성인식 + 즐겨찾기 사용자에게 송금
	public void transferToBookMarkUser(BookMark bookMark, User sender, Long amount) {
		String recipient_name = bookMark.getBookMarkName();
		String recipient_banknumber = bookMark.getBookMarkAccountNumber();
		String category = "transfer";

		TransferDto transferDto = new TransferDto(); // transfer Dto Create
		transferDto.setAmount(amount);
		transferDto.setCategory(category);
		transferDto.setRecipient_banknumber(recipient_banknumber);
		transferDto.setRecipient_name(recipient_name);
		transferDto.setUser(sender);
		transferDto.setSender_banknumber(sender.getBankAccounts().get(0).getAccountNumber());
		transferDto.setSender_name(sender.getUsername());

		System.out.println(transferDto);
		System.out.println("sender name2" + transferDto.getSender_name());

		transferToUser(transferDto, sender);
}

//	{
//
//		"recipient_name": "정준원",
//			"recipient_banknumber": "1",
//			"sender_banknumber": "11",
//			"account_password": "2",
//			"amount": 1
//	}

	@Transactional
	public void transferToUser(TransferDto transferDto, User sender) {
		// TransferDto
		System.out.println(transferDto.toString());

		try {
			// 비관적 락을 사용하여 계좌 정보를 가져옵니다.
			BankAccount recipient_account = bankAccountRepository.findByAccountNumberWithLock( transferDto.getRecipient_banknumber());

			BankAccount mybankAccount = bankAccountRepository.findByAccountNumber(transferDto.getSender_banknumber());


			System.out.println("senderbanknum: " + transferDto.getSender_banknumber().toString());
			System.out.println("sendername: " + transferDto.getSender_name());
			System.out.println(mybankAccount.toString());

			Long amount = mybankAccount.getAmount(); // 본인돈
			Long sendamount = transferDto.getAmount(); // 보낼 돈 액수

			Long originalAmount = mybankAccount.getAmount(); // 송금 전 잔액 저장
            try {
                if (amount - sendamount < 0) {
                    System.out.println("잔액 부족");
                } else {
                    mybankAccount.setAmount(amount - sendamount); // 본인계좌

                    String recipientName = transferDto.getRecipient_name();
                    User reciever = userService.getUserByUsernameAndBankAccount(recipientName, transferDto.getRecipient_banknumber()); // 받는사람

                    System.out.println("recipientName" + recipientName);

                    System.out.println("reciever" + reciever.getUserid());

					// 받는 사람의 현재 잔액을 가져옴
					Long recipientCurMoney = recieveraccount.getAmount();

                    BankAccount recieveraccount = this.getBankAccountByAccountnumber(recipient_account.getAccountNumber());

                    recipientCurMoney = recieveraccount.getAmount(); // 받는사람 현재 잔액

					if (recieveraccount != null) {
						recieveraccount.setAmount(recipientCurMoney + sendamount); // 받는사람 계좌 돈 증가
						updateBankAccount(mybankAccount.toDto()); // 내 계좌에 돈이 빠졌으니 업데이트
						updateBankAccount(recieveraccount.toDto()); // 받는사람 계좌도 업데이트

						Log logentity = transferDto.toEntity(); // 송금
						TransferDto transferGetDto = TransferDto.builder()
								.amount(transferDto.getAmount())
								.category("입금")
								.recipient_banknumber(transferDto.getRecipient_banknumber())
								.sender_banknumber(transferDto.getSender_banknumber())
								.sender_name(transferDto.getSender_name())
								.recipient_name(transferDto.getRecipient_name())
								// .user(reciever)
								.build();

						System.out.println("transfergetdto sendername: \t" + transferGetDto.getSender_name());

						Log logentityGet = transferGetDto.toEntity(); // 입금
						System.out.println("logentityGet  : " + logentityGet.getUser().getUserid());

						logService.save(logentity);
						logService.save(logentityGet);
					}
                }
            } catch (Exception e) {
                // 예외 발생 시 원래 잔액으로 복구
                mybankAccount.setAmount(originalAmount);
                System.err.println("예외 발생, 잔액 복구: " + e.getMessage());
                throw e; // 예외 다시 던지기
            }


		} catch (PessimisticLockingFailureException e) {
			System.err.println("락 획득 실패: " + e.getMessage());
		}
		catch (DataAccessException e) {
			// 데이터베이스 접근 중 오류 발생
			System.err.println("데이터베이스 접근 오류: " + e.getMessage());
		} catch (Exception e) {
			// 다른 예외 처리
			System.err.println("예상치 못한 오류 발생: " + e.getMessage());
		}



	}


	public void setmainAccount(BankAccountDto bankAccountDto) {
		System.out.println("mainaccount");
		System.out.println("mainaccount test " + bankAccountDto.toString());

		List<BankAccount> bankAccounts = new ArrayList();
		bankAccounts = bankAccountRepository.findAll();

		for (BankAccount bankAccount : bankAccounts) {
			if (bankAccount.isMainAccount()) {
				bankAccount.setMainAccount(false);
				bankAccountRepository.save(bankAccount);
			}

		}

		Optional<BankAccount> bankAccount = bankAccountRepository.findById(bankAccountDto.getId());
		System.out.println("bankaccount service" + bankAccount.get().toString());
		bankAccount.get().setMainAccount(true);
		System.out.println("bankaccount service modified" + bankAccount.get().toString());

		bankAccountRepository.save(bankAccount.get());
		System.out.println("fin");

	}

	public BankAccount getBankAccountById(Long id) {
		return bankAccountRepository.findById(id).orElse(null);
	}

	public List<BankAccount> getBankAccountByuserId(User user) {

		String userid = user.getUserid();

		List<BankAccount> bankAccounts = bankAccountRepository.findAll();
		List<BankAccount> bankAccounts2 = new ArrayList<BankAccount>();

		if (bankAccounts.isEmpty()) {
			return null;
		} else {

			for (BankAccount bankAccount : bankAccounts) {
				if (bankAccount.getUser().getUserid().equals(userid)) {
					bankAccounts2.add(bankAccount);
				}
			}

			return bankAccounts2;
		}
	}

}