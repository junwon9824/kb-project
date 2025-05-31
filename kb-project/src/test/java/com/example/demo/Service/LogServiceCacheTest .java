package com.example.demo.service;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.cache.CacheManager;
import org.springframework.test.annotation.DirtiesContext;

import com.example.demo.dto.LogDto;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD) // 캐시 초기화 보장
class LogServiceCacheTest {

    @Autowired
    private LogService logService;

    @SpyBean
    private LogService spyLogService; // getLogsWithoutCache 호출 횟수 추적용

    @Autowired
    private CacheManager cacheManager;

    @Test
    void 캐시_적용_테스트() {
        String userId = "junwon9824";
        String bankNumber = "9824";

        // 캐시 미스: 실제 DB 조회 (getLogsWithoutCache 1회 호출)
        List<LogDto> firstCall = logService.getLogs(userId, bankNumber);

        // 캐시 히트: DB 조회 없이 캐시에서 반환 (getLogsWithoutCache 호출 안 됨)
        List<LogDto> secondCall = logService.getLogs(userId, bankNumber);

        // 결과 비교
        assertThat(firstCall).isEqualTo(secondCall);

        // getLogsWithoutCache는 1번만 호출되어야 함
        Mockito.verify(spyLogService, Mockito.times(1))
                .getLogsWithoutCache(userId, bankNumber);
    }

    @Test
    void 캐시_미사용_테스트() {
        String userId = "junwon9824";
        String bankNumber = "9824";

        // 매번 DB 직접 조회 (getLogsWithoutCache 2회 호출)
        List<LogDto> firstCall = logService.getLogsWithoutCache(userId, bankNumber);
        List<LogDto> secondCall = logService.getLogsWithoutCache(userId, bankNumber);

        // getLogsWithoutCache는 2번 호출되어야 함
        Mockito.verify(spyLogService, Mockito.times(2))
                .getLogsWithoutCache(userId, bankNumber);
    }

    @Test
    void 캐시_무효화_테스트() {
        String userId = "junwon9824";
        String bankNumber = "9824";

        // 캐시 저장
        logService.getLogs(userId, bankNumber);

        // 캐시가 존재하는지 확인
        assertThat(cacheManager.getCache("logCache").get(userId + "-" + bankNumber)).isNotNull();

        // 로그 저장(캐시 무효화 발생)
        logService.save( /* Log 객체 생성해서 전달 */ null);

        // 캐시가 비워졌는지 확인 (null이어야 함)
        assertThat(cacheManager.getCache("logCache").get(userId + "-" + bankNumber)).isNull();
    }
}
