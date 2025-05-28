package com.example.demo.service;

import com.example.demo.dto.*;
import com.example.demo.entity.*;
import com.example.demo.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.*;
import org.springframework.cache.annotation.*;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class LogService {

    private final LogRepository logRepository;
    private final UserService userService;

    @Autowired
    public LogService(LogRepository logRepository, UserService userService) {
        this.logRepository = logRepository;
        this.userService = userService;
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

    /**
     * 캐시된 로그 조회
     */
    @Cacheable(value = "logCache", key = "#userId + '-' + #bankNumber", unless = "#result == null")
    public List<LogDto> getLogs(String userId, String bankNumber) {
        log.info("Cache miss: Fetching logs for user={} bankNumber={}", userId, bankNumber);
        return getLogsWithoutCache(userId, bankNumber);
    }
}
