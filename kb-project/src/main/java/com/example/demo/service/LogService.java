package com.example.demo.service;

import com.example.demo.dto.*;
import com.example.demo.entity.*;
import com.example.demo.repository.*;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.transaction.Transactional;
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

    public void depositSave(Log log) {
        logRepository.save(log);

        // 입금이므로 recipientAccount만 캐시 삭제
        if (log.getRecipientAccount() != null && log.getRecipientAccount().getUser() != null) {
            String recipientKey = log.getRecipientAccount().getUser().getUserid() + "-" + log.getRecipientBankNumber();
            redisTemplate.delete(recipientKey);
        }

    }

    @Transactional
    public void transfersave(Log log) {

        logRepository.save(log);

        // 직접 캐시 삭제
//        String senderKey = log.getSenderAccount().getUser().getUserid() + "-" + log.getSenderBankNumber();
//        String recipientKey = log.getRecipientAccount().getUser().getUserid() + "-" + log.getRecipientBankNumber();

        String senderKey = log.getSenderUserId() + "-" + log.getSenderBankNumber();
        String recipientKey = log.getRecipientUserId() + "-" + log.getRecipientBankNumber();

        redisTemplate.delete(senderKey);
        redisTemplate.delete(recipientKey);
    }

    /**
     * DB에서 직접 조회 (senderAccount 또는 recipientAccount의 계좌번호와 일치하는 로그) => User는
     * 인증된 사용자용, 여기서는 검증 로직에 따라 필터링 추가 가능
     */
    @Transactional
    public List<LogDto> getLogsWithoutCache(String userId, String bankNumber) {
        log.info("useridddd"+userId);
        List<Log> logs = logRepository.findBySenderOrRecipientAccountNumberWithUser(bankNumber);

        log.info("afterfindBySenderOrRecipientAccountNumberWithUser"+userId );

        return logs.stream()
                .filter(log ->( log.getSenderUserId() != null && log.getSenderUserId().equals(userId) && log.getCategory().equals("송금"))
                        || (log.getRecipientUserId()!=null &&  log.getRecipientUserId().equals(userId)&& log.getCategory().equals("입금")) )
                .map(LogDto::fromEntity)
                .collect(Collectors.toList());

    }


//    public List<LogDto> getLogsWithoutCache(String userId, String bankNumber) {
//        // 예: 리포지토리 메서드 호출 (직접 쿼리 만들어야 함)
//        List<Log> logs = logRepository.findBySenderAccountAccountNumberOrRecipientAccountAccountNumber(bankNumber, bankNumber);
//
//        // userId로 필터링(예: 송금/수신자 계좌 소유자가 맞는지)
//        List<Log> filtered = logs.stream()
//                .filter(log -> log.getSenderAccount().getUser().getUserid().equals(userId)
//                || log.getRecipientAccount().getUser().getUserid().equals(userId))
//                .sorted(Comparator.comparing(Log::getCreatedDate).reversed())
//                .collect(Collectors.toList());
//
//        return filtered.stream()
//                .map(log -> LogDto.builder()
//                .amount(log.getAmount())
//                .recipient_banknumber(log.getRecipientBankNumber())
//                .category(log.getCategory())
//                .sender_banknumber(log.getSenderBankNumber())
//                .recipient_name(log.getRecipientName())
//                .sender_name(log.getSenderName())
//                .createdDate(log.getCreatedDate())
//                .build())
//                .collect(Collectors.toList());
//    }

    @CircuitBreaker(name = "redisCircuitBreaker", fallbackMethod = "getLogsFallback")
    public List<LogDto> getLogs(String userId, String bankNumber) {
        try {
            String cacheKey = userId + "-" + bankNumber;
            Object cachedObj = redisTemplate.opsForValue().get(cacheKey);

            log.info("cachedObj"+cachedObj);

            if (cachedObj != null && !((List<?>)cachedObj).isEmpty()) {
                log.info("cachedObj in if"+cachedObj);

                // 1. 먼저 JSON 문자열로 변환
                String json = objectMapper.writeValueAsString(cachedObj);
                // 2. List<LogDto>로 역직렬화
                List<LogDto> cached = objectMapper.readValue(json, new TypeReference<List<LogDto>>() {});
                return cached;

            }

            log.info("userId in getLogs "+userId);
            // 캐시 미스
            List<LogDto> dbResult = getLogsWithoutCache(userId, bankNumber);
            log.info("dbResult"+dbResult);

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
