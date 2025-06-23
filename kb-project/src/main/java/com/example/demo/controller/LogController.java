package com.example.demo.controller;

import com.example.demo.dto.LogDto;
import com.example.demo.entity.User;
import com.example.demo.service.LogService;
import com.example.demo.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/log")
public class LogController {

	private final LogService logService;
	private final UserService userService;

	public LogController(LogService logService, UserService userService) {
		this.logService = logService;
		this.userService = userService;
	}

	// 계좌 로그 조회 (캐시 사용)
	@GetMapping("/{accountNumber}")
	public ResponseEntity<List<LogDto>> getLogs(
			@PathVariable("accountNumber") String accountNumber,
			Principal principal // Spring Security 인증 사용자 정보
	) {
		User user = userService.getUserByUserId(principal.getName());
		List<LogDto> logs = logService.getLogs(user.getUserid(), accountNumber);
		return ResponseEntity.ok(logs);
	}

	// 계좌 로그 조회 (캐시 미사용)
	@GetMapping("/nocache/{accountNumber}")
	public ResponseEntity<List<LogDto>> getLogsWithoutCache(
			@PathVariable("accountNumber") String accountNumber,
			Principal principal
	) {
		User user = userService.getUserByUserId(principal.getName());
		List<LogDto> logs = logService.getLogsWithoutCache(user.getUserid(), accountNumber);
		return ResponseEntity.ok(logs);
	}
}
