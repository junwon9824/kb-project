package com.example.demo;

import com.example.demo.dto.LogDto;
import com.example.demo.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.context.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatCode;

@SpringBootTest
public class CacheErrorHandlerIntegrationTest {

    @Transactional
    @Test
    void getlogs호출 () {
        String userId = "junwon9824";
        String bankNumber = "9824";

        long startNoCache = System.nanoTime();
        List<LogDto> noCache = logService.getLogs(userId, bankNumber);
        long endNoCache = System.nanoTime();
        long durationNoCache = endNoCache - startNoCache;

    }

    @Autowired
    private LogService logService;

    @Test
    @Transactional
    void redis_장애_상황에서도_DB_조회_정상_동작() {
        // Redis 서버를 미리 꺼둔 상태에서 실행
        String userId = "junwon9824";
        String bankNumber = "9824";
        // 캐시 미스 시 DB에서 조회되어 예외 없이 동작해야 함
        assertThatCode(() -> logService.getLogs(userId, bankNumber))
                .doesNotThrowAnyException();
    }
}
