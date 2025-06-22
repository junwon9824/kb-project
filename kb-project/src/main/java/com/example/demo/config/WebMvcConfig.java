package com.example.demo.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HandlerInterceptor() {
                    @Override
                    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
                        return HandlerInterceptor.super.preHandle(request, response, handler);
                    }
                })
                .addPathPatterns("/**") // 모든 경로에 적용
                .excludePathPatterns(
                        "/users/login",
                        "/users/logout",
                        "/users",
                        "/users/new",
                        "/login",       // 프론트엔드 로그인 페이지
                        "/signup",      // 프론트엔드 회원가입 페이지
                        "/users/login",
                        "/users/new",
                        "/css/**",
                        "/js/**",
                        "/assets/**",
                        "/css/**",
                        "/js/**",
                        "/assets/**"
                ); // 인증 없이 접근 가능한 경로 예외 처리
    }
}
