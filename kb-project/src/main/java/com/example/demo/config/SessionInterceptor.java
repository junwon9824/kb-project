package com.example.demo.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.web.servlet.HandlerInterceptor;

public class SessionInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false); // 세션을 가져옴 (세션이 없으면 null 반환)

        if (session == null) {
            System.out.println("no session (null)");
            response.sendRedirect("/users/index");  // 세션이 없으면 로그인 페이지로 리다이렉트
            return false;  // 요청 처리 중지
        }

        Object user = session.getAttribute("user");  // 세션에서 user 객체를 가져옴
        if (user == null) {
            System.out.println("session exists, but no user attribute");
            response.sendRedirect("/users/index");  // 세션은 있지만 user 정보가 없으면 로그인 페이지로 리다이렉트
            return false;
        }

        return true;  // 세션이 유효하고 user 정보가 있으면 요청 처리 계속 진행
    }
}
