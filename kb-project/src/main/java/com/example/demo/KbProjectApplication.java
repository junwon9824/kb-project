package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching // 반드시 추가
@EntityScan(basePackages = "com.example.demo.entity") // 이거 추가

public class KbProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(KbProjectApplication.class, args);
	}
}