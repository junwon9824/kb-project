package com.example.demo.controller;

import com.example.demo.dto.BankAccountDto;
import com.example.demo.entity.Bank;
import com.example.demo.entity.BankAccount;
import com.example.demo.entity.User;
import com.example.demo.service.BankAccountService;
import com.example.demo.service.BankService;
import com.example.demo.service.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bankaccounts")
public class BankAccountController {

    private static final Logger log = LoggerFactory.getLogger(BankAccountController.class);

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
    public List<Map<String, Object>> getBankAccountsByUser(@PathVariable String userid) {
        log.info("=== 계좌 목록 조회 요청 시작 ===");
        log.info("요청된 userid: {}", userid);
        
        try {
            // 데이터베이스에 있는 모든 사용자 조회 (디버깅용)
            List<User> allUsers = userService.getAllUsers();
            log.info("=== 데이터베이스의 모든 사용자 ===");
            for (User u : allUsers) {
                log.info("사용자: id={}, userid={}, username={}", u.getId(), u.getUserid(), u.getUsername());
            }
            log.info("=== 모든 사용자 목록 끝 ===");
            
            User user = userService.getUserByUserId(userid);
            if (user == null) {
                log.error("사용자를 찾을 수 없습니다: userid={}", userid);
                throw new RuntimeException("사용자를 찾을 수 없습니다: " + userid);
            }
            log.info("사용자 조회 성공: userid={}, username={}, id={}", user.getUserid(), user.getUsername(), user.getId());
            
            List<BankAccount> bankAccounts = bankAccountService.getBankAccountByUser(user);
            log.info("계좌 목록 조회 성공: 계좌 개수={}", bankAccounts.size());
            
            // 각 계좌 정보를 자세히 로그로 출력
            for (BankAccount account : bankAccounts) {
                log.info("계좌 정보: id={}, accountNumber={}, amount={}, mainAccount={}, bank={}, userId={}", 
                    account.getId(), account.getAccountNumber(), account.getAmount(), 
                    account.isMainAccount(), account.getBank() != null ? account.getBank().getBankname() : "null",
                    account.getUser() != null ? account.getUser().getUserid() : "null");
            }
            
            // BankAccount 엔티티를 Map으로 변환 (수동 getter 사용)
            List<Map<String, Object>> result = bankAccounts.stream()
                    .map(account -> {
                        Map<String, Object> accountMap = new java.util.HashMap<>();
                        accountMap.put("id", account.getId());
                        accountMap.put("accountNumber", account.getAccountNumber());
                        accountMap.put("amount", account.getAmount());
                        accountMap.put("mainAccount", account.isMainAccount());
                        accountMap.put("bankName", account.getBank() != null ? account.getBank().getBankname() : null);
                        return accountMap;
                    })
                    .collect(java.util.stream.Collectors.toList());
            
            log.info("계좌 목록 변환 완료: {}", result);
            log.info("=== 계좌 목록 조회 요청 완료 ===");
            return result;
            
        } catch (Exception e) {
            log.error("계좌 목록 조회 실패: userid={}, error={}", userid, e.getMessage(), e);
            throw e;
        }
    }

    // 2. 계좌 생성 (POST)
    @PostMapping
    public BankAccount createBankAccount(@RequestBody BankAccount bankAccount, @RequestParam String userid) {
        try {
            User user = userService.getUserByUserId(userid);
            String bankname = bankAccount.getBank().getBankname();
            Bank bank = bankService.getBankBybankname(bankname);
            
            // 중복 계좌 검증
            BankAccount existingAccount = bankAccountService.getBankAccountByAccountnumber(bankAccount.getAccountNumber());
            if (existingAccount != null) {
                throw new RuntimeException("동일한 계좌번호가 이미 존재합니다: " + bankAccount.getAccountNumber());
            }
            
            return bankAccountService.createBankAccount(bankAccount, bank, user);
        } catch (RuntimeException e) {
            log.error("계좌 생성 실패: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("계좌 생성 중 예외 발생: {}", e.getMessage(), e);
            throw new RuntimeException("계좌 생성에 실패했습니다: " + e.getMessage());
        }
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

