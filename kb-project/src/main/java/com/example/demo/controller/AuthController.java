package com.example.demo.controller;

import com.example.demo.dto.Login;
import com.example.demo.security.JwtTokenProvider;
import com.example.demo.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<String> login(@RequestBody Login loginRequest) {
        // 인증 시 userid와 password 사용 (Login DTO에 username 대신 userid가 있음)
        if (userService.authenticate(loginRequest.getUserid(), loginRequest.getPassword())) {
            // 토큰 생성 시 userid 사용 (권한은 예시로 "ROLE_USER"를 넣음)
            String token = jwtTokenProvider.createToken(loginRequest.getUserid(), List.of("ROLE_USER"));
            return ResponseEntity.ok(token);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }
}
