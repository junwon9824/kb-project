package com.example.demo.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secretKey;

    private Key key;

    private final long validityInMilliseconds = 3600000; // 1시간

    @PostConstruct
    protected void init() {
        key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretKey));
    }

    public String createToken(String userid, List<String> roles) {
        Claims claims = Jwts.claims().setSubject(userid);
        claims.put("roles", roles);

        Date now = new Date();
        Date expiry = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }


    public boolean validateToken(String token) {
        try {
            Jws<Claims> claimsJws = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            boolean expired = claimsJws.getBody().getExpiration().before(new Date());
            if (expired) {
                log.warn("JWT 만료: exp={}, now={}", claimsJws.getBody().getExpiration(), new Date());
            }
            return !expired;
        } catch (ExpiredJwtException e) {
            log.warn("JWT 만료 예외: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.warn("지원하지 않는 JWT: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.warn("잘못된 JWT: {}", e.getMessage());
        } catch (SignatureException e) {
            log.warn("JWT 서명 오류: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.warn("JWT 인자 오류: {}", e.getMessage());
        } catch (Exception e) {
            log.warn("JWT 기타 오류: {}", e.getMessage());
        }
        return false;
    }


    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();

        String userid = claims.getSubject();
        @SuppressWarnings("unchecked")
        List<String> roles = (List<String>) claims.get("roles");

        List<SimpleGrantedAuthority> authorities = roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        return new UsernamePasswordAuthenticationToken(userid, "", authorities);
    }

    public String resolveToken(String bearerToken) {
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public String getUsername(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    public Date getExpirationDateFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getExpiration();
    }

}
