package com.example.demo.controller;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.Map;

import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.Login;
import com.example.demo.dto.TransferDto;
import com.example.demo.dto.UserDto;
import com.example.demo.entity.BankAccount;
import com.example.demo.entity.User;
import com.example.demo.repository.BankAccountRepository;
import com.example.demo.service.BankAccountService;
import com.example.demo.service.LogService;
import com.example.demo.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private LogService logService;

    @Autowired
    private BankAccountService bankaccountservice;

    @Autowired
    private BankAccountRepository bankAccountRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider; // 토큰 생성/파싱 클래스

    private final RedisTemplate redisTemplate;

    @GetMapping("/{id}")
    public User getUser(@PathVariable("id") Long id) {
        return userService.getUserById(id).orElse(null);
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping
    public User createUser(@RequestBody UserDto userDto, HttpServletRequest request) {
        userDto.setClientSafeIp(request.getRemoteAddr());
        return userService.createUser(userDto);
    }

    @PutMapping("/{id}")
    public User updateUser(@PathVariable("id") Long id, @RequestBody UserDto userDto) {
        return userService.updateUser(id, userDto.toEntity());
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable("id") Long id) {
        userService.deleteUser(id);
    }

//	@PostMapping("/users/login")
//	public ResponseEntity<?> login(@RequestBody Login login, HttpSession session) {
//		User user = userService.getUserByUserId(login.getUserid());
//		if (user != null && user.getPassword().equals(login.getPassword())) {
//			session.setAttribute("user", user);
//			return ResponseEntity.ok(user);
//		} else {
//			return ResponseEntity.status(401).body("Invalid credentials");
//		}
//	}

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {

        String bearerToken = request.getHeader("Authorization"); // Authorization 헤더 가져오기
        // 1. 헤더에서 JWT 토큰 추출
        String token = jwtTokenProvider.resolveToken(bearerToken);

        if (token != null && jwtTokenProvider.validateToken(token)) {
            // 2. 토큰의 남은 유효시간 계산 (초 단위)
            Date expiration = jwtTokenProvider.getExpirationDateFromToken(token);
            long remainingSeconds = (expiration.getTime() - System.currentTimeMillis()) / 1000;
            if (remainingSeconds > 0) {
                // 3. 블랙리스트(Redis)에 토큰 추가, TTL은 남은 유효시간으로 설정
                redisTemplate.opsForValue().set(token, "invalid", remainingSeconds, TimeUnit.SECONDS);
            }
            return ResponseEntity.ok("Logged out");
        } else {
            return ResponseEntity.badRequest().body("Invalid token");
        }

    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(HttpServletRequest request) {
        try {
            String bearerToken = request.getHeader("Authorization");
            if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body("Authorization header is missing or invalid");
            }
            
            String token = jwtTokenProvider.resolveToken(bearerToken);
            if (token == null) {
                return ResponseEntity.status(401).body("Invalid token format");
            }
            
            if (!jwtTokenProvider.validateToken(token)) {
                return ResponseEntity.status(401).body("Token is invalid or expired");
            }
            
            String userid = jwtTokenProvider.getUsername(token);
            
            // 간단한 Map으로 필요한 정보만 반환 (JSON 직렬화 문제 방지)
            return ResponseEntity.ok(Map.of(
                "userid", userid,
                "username", "test_user"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal server error: " + e.getMessage());
        }
    }

    // 송금 등 기타 기능은 별도 RESTful 엔드포인트로 분리 필요
}
