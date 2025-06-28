package com.example.demo.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Login {
	private String userid;
	private String password;
	
	// 임시 수동 getter (Lombok 문제 해결용)
	public String getUserid() {
		return this.userid;
	}
	
	public String getPassword() {
		return this.password;
	}
}
