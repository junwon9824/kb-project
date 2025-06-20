package com.example.demo.controller;

import com.example.demo.dto.BankAccountDto;
import com.example.demo.entity.Bank;
import com.example.demo.entity.BankAccount;
import com.example.demo.entity.User;
import com.example.demo.repository.BankAccountRepository;
import com.example.demo.service.BankAccountService;
import com.example.demo.service.BankService;
import com.example.demo.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@RestController
public class BankAccountController {

	private final BankAccountService bankAccountService;
	private final BankService bankService;
	private final UserService userService;

	public BankAccountController(BankAccountService bankAccountService, BankService bankService,
			UserService userService) {
		this.bankAccountService = bankAccountService;
		this.bankService = bankService;
		this.userService = userService;
	}

	@GetMapping("/bankaccounts")
	public List<BankAccount> getBankAccounts(HttpServletRequest request) {
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		return bankAccountService.getBankAccountByUser(user);
	}

	@PostMapping("/bankaccounts")
	public BankAccount createBankAccount(@RequestBody BankAccount bankAccount, HttpServletRequest request) {
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		Bank bank = bankAccount.getBank();
		return bankAccountService.createBankAccount(bankAccount, bank, user);
	}

	@DeleteMapping("/bankaccounts/{id}")
	public void deleteBankAccount(@PathVariable Long id, HttpServletRequest request) {
		BankAccount bankAccount = bankAccountService.getBankAccountById(id);
		bankAccountService.deleteBankAccount(bankAccount);
	}

	@PutMapping("/bankaccounts/mainAccount/{id}")
	public void setMainAccount(@PathVariable("id") Long id) {
		BankAccountDto bankAccountDto = bankAccountService.getBankAccountByAccountId(id).toDto();
		bankAccountService.setmainAccount(bankAccountDto);
	}
}
