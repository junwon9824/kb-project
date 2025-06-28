package com.example.demo.debug;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class DatabaseLogger {

    @Autowired
    private Environment env;

    @PostConstruct
    public void logDatabaseUrl() {
        String url = env.getProperty("spring.datasource.url");
        String user = env.getProperty("spring.datasource.username");
        System.out.println("==== DB 연결 정보 ====");
        System.out.println("URL: " + url);
        System.out.println("USER: " + user);
        System.out.println("=====================");
    }
}

