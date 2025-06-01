package com.example.demo.Service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.cache.CacheManager;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.LogDto;
import com.example.demo.service.LogService;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional
public class LogServiceCachePerformanceTest {

    @Autowired
    private LogService logService;

    @SpyBean
    private LogService spyLogService;

    @Autowired
    private CacheManager cacheManager;

    @Test
    void 캐시_성능_비교_테스트() {
        String userId = "junwon9824";
        String bankNumber = "9824";

        // 캐시 미사용 (첫 호출)
        long startNoCache = System.nanoTime();
        List<LogDto> noCache = logService.getLogsWithoutCache(userId, bankNumber);
        long endNoCache = System.nanoTime();
        long durationNoCache = endNoCache - startNoCache;

        // 캐시 적용 (첫 호출: 캐시에 저장)
        long startWithCacheFirst = System.nanoTime();
        List<LogDto> withCacheFirst = logService.getLogs(userId, bankNumber);
        long endWithCacheFirst = System.nanoTime();
        long durationWithCacheFirst = endWithCacheFirst - startWithCacheFirst;

        // 캐시 적용 (두 번째 호출: 캐시에서 조회)
        long startWithCacheSecond = System.nanoTime();
        List<LogDto> withCacheSecond = logService.getLogs(userId, bankNumber);
        long endWithCacheSecond = System.nanoTime();
        long durationWithCacheSecond = endWithCacheSecond - startWithCacheSecond;

        System.out.println("캐시 미사용(직접 DB): " + durationNoCache / 1_000_000.0 + " ms");
        System.out.println("캐시 적용(첫 호출): " + durationWithCacheFirst / 1_000_000.0 + " ms");
        System.out.println("캐시 적용(두 번째 호출, 캐시 hit): " + durationWithCacheSecond / 1_000_000.0 + " ms");

        // 성능 비교를 위한 단순 assert (캐시 hit이 가장 빨라야 함)
        assertThat(durationWithCacheSecond).isLessThan(durationNoCache);
    }
}
