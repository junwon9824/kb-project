package com.example.demo.controller;

import com.example.demo.dto.BankAccountDto;
import com.example.demo.entity.Bank;
import com.example.demo.entity.BankAccount;
import com.example.demo.entity.User;
import com.example.demo.service.BankAccountService;
import com.example.demo.service.BankService;
import com.example.demo.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bankaccounts")
public class BankAccountController {

    private final BankAccountService bankAccountService;
    private final BankService bankService;
    private final UserService userService;

    @Autowired
    public BankAccountController(BankAccountService bankAccountService, BankService bankService,
                                 UserService userService) {
        this.bankAccountService = bankAccountService;
        this.bankService = bankService;
        this.userService = userService;
    }

    // 1. 특정 유저의 계좌 목록 조회 (userid로 조회)
    @GetMapping("/user/{userid}")
    public List<BankAccount> getBankAccountsByUser(@PathVariable String userid) {
        User user = userService.getUserByUserId(userid);
        return bankAccountService.getBankAccountByUser(user);
    }

    // 2. 계좌 생성 (POST)
    @PostMapping
    public BankAccount createBankAccount(@RequestBody BankAccount bankAccount, @RequestParam String userid) {
        User user = userService.getUserByUserId(userid);
        String bankname = bankAccount.getBank().getBankname();
        Bank bank = bankService.getBankBybankname(bankname);
        return bankAccountService.createBankAccount(bankAccount, bank, user);
    }

    // 3. 계좌 단건 조회
    @GetMapping("/{id}")
    public BankAccount getBankAccount(@PathVariable Long id) {
        return bankAccountService.getBankAccountById(id);
    }

    // 4. 계좌 삭제 (DELETE)
    @DeleteMapping("/{id}")
    public void deleteBankAccount(@PathVariable Long id) {
        BankAccount bankAccount = bankAccountService.getBankAccountById(id);
        bankAccountService.deleteBankAccount(bankAccount);
    }

    // 5. 주계좌 설정 (PUT)
    @PutMapping("/mainAccount/{id}")
    public void setMainAccount(@PathVariable Long id) {
        BankAccountDto bankAccountDto = bankAccountService.getBankAccountByAccountId(id).toDto();
        bankAccountService.setmainAccount(bankAccountDto);
    }

    // 필요하다면 추가적인 RESTful 엔드포인트도 정의 가능
}

