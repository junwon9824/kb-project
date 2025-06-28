package com.example.demo.security;

import jakarta.servlet.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtTokenFilter.class);
    
    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;

    public JwtTokenFilter(JwtTokenProvider jwtTokenProvider, UserDetailsService userDetailsService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String path = request.getRequestURI();
        String method = request.getMethod();

        log.info("=== JWT 필터 시작: {} {} ===", method, path);

        // 인증이 필요하지 않은 경로들 (SecurityConfig와 일치)
        if (path.equals("/api/auth/login") || 
            (path.equals("/api/users") && method.equals("POST"))) {
            log.info("인증 불필요 경로: {} {}", method, path);
            chain.doFilter(request, response);
            return;
        }

        String header = request.getHeader("Authorization");
        log.info("Authorization 헤더: {}", header);
        
        String token = jwtTokenProvider.resolveToken(header);
        log.info("추출된 토큰: {}", token != null ? token.substring(0, Math.min(50, token.length())) + "..." : "null");

        if (token != null && jwtTokenProvider.validateToken(token)) {
            log.info("토큰 검증 성공");
            String username = jwtTokenProvider.getUsername(token);
            log.info("토큰에서 추출한 사용자명: {}", username);
            
            try {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                log.info("UserDetails 로드 성공: {}", userDetails.getUsername());
                log.info("권한 목록: {}", userDetails.getAuthorities());

                UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);

                log.info("JWT 인증 성공: user={}, uri={}", username, path);
            } catch (Exception e) {
                log.error("UserDetails 로드 실패: {}", e.getMessage(), e);
            }

        } else {
            log.warn("JWT 인증 실패: uri={}, token={}", path, token != null ? "present" : "null");
            if (token != null) {
                log.warn("토큰 검증 실패 - 토큰이 유효하지 않음");
            } else {
                log.warn("토큰이 없음");
            }
        }

        chain.doFilter(request, response);
        log.info("=== JWT 필터 종료 ===");
    }
}

