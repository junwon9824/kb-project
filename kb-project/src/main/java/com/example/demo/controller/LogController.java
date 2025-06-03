package com.example.demo.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.demo.dto.LogDto;
import com.example.demo.entity.User;
import com.example.demo.service.BankAccountService;
import com.example.demo.service.BankService;
import com.example.demo.service.LogService;
import com.example.demo.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
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

	@GetMapping("/log/{myaccountnumber}") //
	public String getlogs(@PathVariable("myaccountnumber") String myaccountnumber, Model model,
			HttpServletRequest request) {

		HttpSession session = request.getSession();

		/// // jmeter 하드코딩
		// User userByUserId = userService.getUserByUserId("junho1131" );

		// System.out.println("userByUserIduserByUserId"+userByUserId.getUserid());

		// session.setAttribute("user", userByUserId);
		/// // jmeter 하드코딩

		User user = (User) session.getAttribute("user");

		System.out.println("getlogof my account" + myaccountnumber);

		List<LogDto> logs = logService.getLogs(user.getUserid(), myaccountnumber);

		System.out.println("myaccountnumber" + myaccountnumber);
		System.out.println("logs" + logs.toString());
		model.addAttribute("Log", logs);

		if (user.isDisabled()) {// 장애인

			return "log/logs2";

		} else {// 비장애인
			return "log/logs";
		}

	}

	@GetMapping("/withoutcache/log/{myaccountnumber}") //
	public String getlogswithoutcahce(@PathVariable("myaccountnumber") String myaccountnumber, Model model,
			HttpServletRequest request) {

		HttpSession session = request.getSession();

		/// // jmeter 하드코딩
		// User userByUserId = userService.getUserByUserId("junho1131" );
		//
		// System.out.println("userByUserIduserByUserId"+userByUserId.getUserid());
		//
		// session.setAttribute("user", userByUserId);
		/// // jmeter 하드코딩

		User user = (User) session.getAttribute("user");

//		System.out.println("getlogof my account" + myaccountnumber);

		List<LogDto> logs = logService.getLogsWithoutCache(user.getUserid(), myaccountnumber);

		System.out.println("myaccountnumber" + myaccountnumber);
		System.out.println("logs" + logs.toString());
		model.addAttribute("Log", logs);

		if (user.isDisabled()) {// 장애인

			return "log/logs2";

		} else {// 비장애인
			return "log/logs";
		}

	}

	// 다른 HTTP 요청에 대한 메서드 작성 (계좌 생성, 수정, 삭제 등)
}
