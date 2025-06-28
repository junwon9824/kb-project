package com.example.demo.controller;

import com.example.demo.entity.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/voice")
public class VoiceRecogController {
    public VoiceRecogController() {
    }

    @GetMapping("/voiceRecognition")
    public String voiceRecognition(HttpServletRequest request) {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        return user.isDisabled() ? "장애인 음성인식" : "비장애인 음성인식";
    }
}
