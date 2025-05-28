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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
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

	@GetMapping("/bankaccounts") // User Bank Account List
	public String getBankAccounts(Model model, HttpServletRequest request) {
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		List<BankAccount> bankAccounts = bankAccountService.getBankAccountByUser(user);

		model.addAttribute("bankAccounts", bankAccounts);

		if (user.isDisabled()) {// 장애인

			return "bankaccount/list2";

		} else {// 비장애인
			return "bankaccount/list";

		}

	}

	// User Bank Account Create
	// User Clients send Data for Bank Accounts
	@PostMapping("/bankaccounts")
	public String createBankAccount(@ModelAttribute("bankAccount") BankAccount bankAccount,
			HttpServletRequest request) {
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		String userid = user.getUserid();

		String bankname = bankAccount.getBank().getBankname();
		Bank bank = bankService.getBankBybankname(bankname);

		System.out.println("user" + userid + " make account getAccountNumber" + bankAccount.getAccountNumber());
		bankAccountService.createBankAccount(bankAccount, bank, user);

		return "redirect:/bankaccounts";

	}
	// accountNumber=${accountNumber}
	// amount=
	// bank_id=1
	// mainAccount=true

	// User Bank Account Create FormPage
	@GetMapping("/bankaccounts/create")
	public String createBankAccountform(Model model) {
		model.addAttribute("bankAccount", new BankAccount());

		return "bankaccount/create";

	}

	@GetMapping("/bankaccounts/create2")
	public String createBankAccountform2(Model model) {
		model.addAttribute("bankAccount", new BankAccount());

		return "bankaccount/create2";

	}

	// 계좌 생성
	@PostMapping("/bankaccounts/create")
	public String createBankAccount(HttpServletRequest request,
			@ModelAttribute("bankaccount") BankAccount bankAccount) {
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");

		String userid = user.getUserid();
		String bankname = bankAccount.getBank().getBankname();
		Bank bank = bankService.getBankBybankname(bankname);

		bankAccountService.createBankAccount(bankAccount, bank, user);
		return "redirect:/bankaccounts";

	}

	@GetMapping("/bankaccounts/delete/{id}") // 삭제
	public String deleteBankAccount(@PathVariable Long id, HttpServletRequest request) {
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");

		BankAccount bankAccount = bankAccountService.getBankAccountById(id);
		Long bankid = bankAccount.getBank().getId();
		Bank bankById = bankService.getBankById(bankid);
		System.out.println(bankAccount.getId());
		bankAccountService.deleteBankAccount(bankAccount);
		System.out.println("delete");

		return "redirect:/bankaccounts";

	}

	@GetMapping("/bankaccounts/mainAccount/{id}") // 주계좌설정
	public String setMainAccount(@PathVariable("id") Long id) {

		BankAccountDto bankAccountDto = bankAccountService.getBankAccountByAccountId(id).toDto();
		System.out.println("controller bankaccount" + bankAccountDto.toString());
		bankAccountService.setmainAccount(bankAccountDto);
		System.out.println("success modify");

		return "redirect:/bankaccounts";

	}

	// 다른 HTTP 요청에 대한 메서드 작성 (계좌 생성, 수정, 삭제 등)
}
