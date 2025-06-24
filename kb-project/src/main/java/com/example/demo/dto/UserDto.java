package com.example.demo.dto;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;

import com.example.demo.entity.BankAccount;
import com.example.demo.entity.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserDto {
	private Long id;
	private String username;
	private String userid;
	private String password;
	private String phone;
	private String address;
	private String account_password;
	private String clientSafeIp;

	private boolean disabled;

	private List<BankAccount> bankAccounts = new ArrayList<>();

	public User toEntity() {
		return User.builder().username(username)
				.userid(userid)
				.password(password)
				.phone(phone)
				.address(address)
				.disabled(disabled)
				.bankAccounts(bankAccounts)
				.clientSafeIp(clientSafeIp).build();

	}
}
