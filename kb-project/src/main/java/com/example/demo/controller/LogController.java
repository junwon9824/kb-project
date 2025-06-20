package com.example.demo.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.LogDto;
import com.example.demo.entity.User;
import com.example.demo.service.BankAccountService;
import com.example.demo.service.BankService;
import com.example.demo.service.LogService;
import com.example.demo.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@RestController
public class LogController {

    private final BankAccountService bankAccountService;
    private final BankService bankService;
    private final UserService userService;
    private final LogService logService;

    public LogController(BankAccountService bankAccountService, BankService bankService, UserService userService,
                         LogService logService) {
        this.bankAccountService = bankAccountService;
        this.bankService = bankService;
        this.userService = userService;
        this.logService = logService;
    }

    @GetMapping("/log/{myaccountnumber}")
    public List<LogDto> getlogs(@PathVariable("myaccountnumber") String myaccountnumber, HttpServletRequest request) {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        return logService.getLogs(user.getUserid(), myaccountnumber);
    }

    @GetMapping("/withoutcache/log/{myaccountnumber}")
    public List<LogDto> getlogswithoutcahce(@PathVariable("myaccountnumber") String myaccountnumber, HttpServletRequest request) {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        return logService.getLogs(user.getUserid(), myaccountnumber);
    }

    // 다른 HTTP 요청에 대한 메서드 작성 (계좌 생성, 수정, 삭제 등)
}
