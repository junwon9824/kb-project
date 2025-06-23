package com.example.demo.controller;

import com.example.demo.dto.Login;
import com.example.demo.security.JwtTokenProvider;
import com.example.demo.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

	private final JwtTokenProvider jwtTokenProvider;
	private final UserService userService;

	// 생성자 주입 (권장)
	public AuthController(JwtTokenProvider jwtTokenProvider, UserService userService) {
		this.jwtTokenProvider = jwtTokenProvider;
		this.userService = userService;
	}

	@PostMapping("/login")
	public ResponseEntity<Map<String, Object>> login(@RequestBody Login loginRequest) {
		if (userService.authenticate(loginRequest.getUserid(), loginRequest.getPassword())) {
			String token = jwtTokenProvider.createToken(loginRequest.getUserid(), List.of("ROLE_USER"));
			Map<String, Object> response = new HashMap<>();
			response.put("token", token);
			response.put("expiresIn", 3600); // 토큰 만료(초), 필요시
			response.put("userId", loginRequest.getUserid());
			return ResponseEntity.ok(response);
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
				.body(Map.of("error", "Invalid credentials"));
		}
	}
}
