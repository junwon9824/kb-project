package com.example.demo.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.example.demo.dto.BankAccountDto;
import com.example.demo.dto.TransferDto;
import com.example.demo.entity.*;
import com.example.demo.repository.BankAccountRepository;
import com.example.demo.repository.LogRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
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

	@Transactional
	public void transferToUser(TransferDto transferDto, User sender) {
		// TransferDto
		System.out.println(transferDto.toString());

		// 비관적 락을 사용하여 계좌 정보를 가져옵니다.
		BankAccount mybankAccount = bankAccountRepository.findByAccountNumberWithLock(transferDto.getSender_banknumber());

		System.out.println("senderbanknum: " + transferDto.getSender_banknumber().toString());
		System.out.println("sendername: " + transferDto.getSender_name());
		System.out.println(mybankAccount.toString());

		Long amount = mybankAccount.getAmount(); // 본인돈
		Long sendamount = transferDto.getAmount(); // 보낼 돈 액수

		if (amount - sendamount < 0) {
			System.out.println("잔액 부족");
		} else {
			mybankAccount.setAmount(amount - sendamount); // 본인계좌

			String recipientName = transferDto.getRecipient_name();
			User user1 = userService.getUserByUsername(recipientName); // 받는사람

			Long recipientCurMoney = 0L;

			BankAccount bankAccount1= this.getBankAccountByAccountnumber( transferDto.getRecipient_banknumber());

			recipientCurMoney = bankAccount1.getAmount(); // 받는사람 현재 잔액
			BankAccount bankAccount = bankAccount1; // 받는사람 계좌

//			for (BankAccount bankAccounts1 : user1.getBankAccounts()) {
//				if (bankAccounts1.getAccountNumber().equals(transferDto.getRecipient_banknumber())) {
//					recipientCurMoney = bankAccounts1.getAmount(); // 받는사람 현재 잔액
//					bankAccount = bankAccounts1; // 받는사람 계좌
//					break; // 계좌를 찾으면 루프 종료
//				}
//			}

			if (bankAccount != null) {
				bankAccount.setAmount(recipientCurMoney + sendamount); // 받는사람 계좌 돈 증가
				updateBankAccount(mybankAccount.toDto()); // 내 계좌에 돈이 빠졌으니 업데이트

				Log logentity = transferDto.toEntity(); // 송금
				TransferDto transferGetDto = TransferDto.builder()
						.amount(transferDto.getAmount())
						.category("입금")
						.recipient_banknumber(transferDto.getRecipient_banknumber())
						.sender_banknumber(transferDto.getSender_banknumber())
						.sender_name(transferDto.getSender_name())
						.recipient_name(transferDto.getRecipient_name())
						.user(user1)
						.build();

				System.out.println("transfergetdto sendername: " + transferGetDto.getSender_name());
				Log logentityGet = transferGetDto.toEntity(); // 입금

				logService.save(logentity);
				logService.save(logentityGet);
			}
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