package com.example.demo.controller;

import com.example.demo.dto.Login;
import com.example.demo.security.JwtTokenProvider;
import com.example.demo.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private static final Logger log = LoggerFactory.getLogger(AuthController.class);
	
	private final JwtTokenProvider jwtTokenProvider;
	private final UserService userService;

	// 생성자 주입 (권장)
	public AuthController(JwtTokenProvider jwtTokenProvider, UserService userService) {
		this.jwtTokenProvider = jwtTokenProvider;
		this.userService = userService;
	}

	@PostMapping("/login")
	public ResponseEntity<Map<String, Object>> login(@RequestBody Login loginRequest) {
		log.info("=== 로그인 요청 시작 ===");
		log.info("요청된 userid: {}", loginRequest.getUserid());
		
		try {
			boolean isAuthenticated = userService.authenticate(loginRequest.getUserid(), loginRequest.getPassword());
			log.info("인증 결과: {}", isAuthenticated);
			
			if (isAuthenticated) {
				String token = jwtTokenProvider.createToken(loginRequest.getUserid(), List.of("ROLE_USER"));
				log.info("JWT 토큰 생성 완료");
				
				Map<String, Object> response = new HashMap<>();
				response.put("token", token);
				response.put("expiresIn", 3600);
				response.put("userId", loginRequest.getUserid());
				
				log.info("로그인 성공 응답 전송");
				log.info("=== 로그인 요청 완료 ===");
				return ResponseEntity.ok(response);
			} else {
				log.warn("인증 실패: 잘못된 비밀번호");
				log.info("=== 로그인 요청 완료 ===");
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(Map.of("error", "Invalid credentials"));
			}
		} catch (NoSuchElementException e) {
			log.error("사용자를 찾을 수 없음: {}", e.getMessage());
			log.info("=== 로그인 요청 완료 ===");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
				.body(Map.of("error", "사용자를 찾을 수 없습니다: " + loginRequest.getUserid()));
		} catch (Exception e) {
			log.error("로그인 처리 중 예외 발생: {}", e.getMessage(), e);
			log.info("=== 로그인 요청 완료 ===");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(Map.of("error", "로그인 처리 중 오류가 발생했습니다: " + e.getMessage()));
		}
	}
	
}
