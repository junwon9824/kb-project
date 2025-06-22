package com.example.demo.controller;

import com.example.demo.dto.Login;
import com.example.demo.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Login loginRequest) {
        // Authenticate user (e.g., check username/password)
        String token = jwtTokenProvider.createToken("userid", List.of("ROLE_USER"));
        return ResponseEntity.ok(token);
    }
}
