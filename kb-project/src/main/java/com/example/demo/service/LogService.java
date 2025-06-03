package com.example.demo.service;

import com.example.demo.dto.*;
import com.example.demo.entity.*;
import com.example.demo.repository.*;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.*;
import org.springframework.cache.annotation.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.type.TypeReference;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Comparator;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import com.fasterxml.jackson.databind.ObjectMapper;

@Slf4j
@Service
public class LogService {

    @Autowired
    private ObjectMapper objectMapper; // Bean으로 주입
    private final LogRepository logRepository;
    private final UserService userService;
    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public LogService(LogRepository logRepository, UserService userService,RedisTemplate<String, Object> redisTemplate ) {
        this.logRepository = logRepository;
        this.userService = userService;
        this.redisTemplate= redisTemplate;
    }

    /**
     * 로그 저장 후 관련된 계좌 캐시 무효화
     */
//    @CacheEvict(value = "logCache", key = "#log.senderAccount.user.userid + '-' + #log.senderBankNumber")
//    @CacheEvict(value = "logCache", key = "#log.recipientAccount.user.userid + '-' + #log.recipientBankNumber")
//    public void save(Log log) {
//        logRepository.save(log);
//    }
    @Caching(evict = {
        @CacheEvict(value = "logCache", key = "#log.senderAccount.user.userid + '-' + #log.senderBankNumber"),
        @CacheEvict(value = "logCache", key = "#log.recipientAccount.user.userid + '-' + #log.recipientBankNumber")
    })
    public void save(Log log) {
        logRepository.save(log);
    }

    /**
     * DB에서 직접 조회 (senderAccount 또는 recipientAccount의 계좌번호와 일치하는 로그) => User는
     * 인증된 사용자용, 여기서는 검증 로직에 따라 필터링 추가 가능
     */
    public List<LogDto> getLogsWithoutCache(String userId, String bankNumber) {
        // 예: 리포지토리 메서드 호출 (직접 쿼리 만들어야 함)
        List<Log> logs = logRepository.findBySenderAccountAccountNumberOrRecipientAccountAccountNumber(bankNumber, bankNumber);

        // userId로 필터링(예: 송금/수신자 계좌 소유자가 맞는지)
        List<Log> filtered = logs.stream()
                .filter(log -> log.getSenderAccount().getUser().getUserid().equals(userId)
                || log.getRecipientAccount().getUser().getUserid().equals(userId))
                .sorted(Comparator.comparing(Log::getCreatedDate).reversed())
                .collect(Collectors.toList());

        return filtered.stream()
                .map(log -> LogDto.builder()
                .amount(log.getAmount())
                .recipient_banknumber(log.getRecipientBankNumber())
                .category(log.getCategory())
                .sender_banknumber(log.getSenderBankNumber())
                .recipient_name(log.getRecipientName())
                .sender_name(log.getSenderName())
                .createdDate(log.getCreatedDate())
                .build())
                .collect(Collectors.toList());
    }
//
//    /**
//     * 캐시된 로그 조회
//     */
//    @Cacheable(value = "logCache", key = "#userId + '-' + #bankNumber", unless = "#result == null")
//    public List<LogDto> getLogs(String userId, String bankNumber) {
//        log.info("Cache miss: Fetching logs for user={} bankNumber={}", userId, bankNumber);
//        return getLogsWithoutCache(userId, bankNumber);
//    }

// ... LogService 내부

    @CircuitBreaker(name = "redisCircuitBreaker", fallbackMethod = "getLogsFallback")
    public List<LogDto> getLogs(String userId, String bankNumber) {
        try {
            String cacheKey = userId + "-" + bankNumber;
            Object cachedObj = redisTemplate.opsForValue().get(cacheKey);
            if (cachedObj != null) {
                // 1. 먼저 JSON 문자열로 변환
                String json = objectMapper.writeValueAsString(cachedObj);
                // 2. List<LogDto>로 역직렬화
                List<LogDto> cached = objectMapper.readValue(json, new TypeReference<List<LogDto>>() {});
                return cached;
            }

            // 캐시 미스
            List<LogDto> dbResult = getLogsWithoutCache(userId, bankNumber);
            redisTemplate.opsForValue().set(cacheKey, dbResult, 1, TimeUnit.HOURS);
            return dbResult;
        } catch (Exception e) {
            log.error("Redis 장애! fallback 동작: user={}, bankNumber={}, 원인={}", userId, bankNumber, e.getMessage(), e);
            throw new RuntimeException("Redis 오류", e);
        }
    }

    public List<LogDto> getLogsFallback(String userId, String bankNumber, Throwable t) {

        log.warn("Redis 장애! fallback 동작: user={}, bankNumber={}, 원인={}", userId, bankNumber, t.toString());
        return getLogsWithoutCache(userId, bankNumber);
    }



}
