package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;

@SpringBootTest
class CacheManagerTypeTest {

    @Autowired
    private CacheManager cacheManager;

    @Test
    void printCacheManagerType() {
        System.out.println("### CacheManager 타입: " + cacheManager.getClass());
    }
}
