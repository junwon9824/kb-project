package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.util.*;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String username;

	@Column(unique = true)
	private String userid;

	@Column(nullable = false)
	@JsonIgnore
	private String password;

	private String clientSafeIp;

	private String phone;
	private String address;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<BankAccount> bankAccounts = new ArrayList<>();

	private boolean disabled;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "role_id")
	private Role role;

	// 수동 getter 메서드들 추가
	public Long getId() {
		return this.id;
	}

	public String getUsername() {
		return this.username;
	}

	public String getUserid() {
		return this.userid;
	}

	public String getPassword() {
		return this.password;
	}

	public String getClientSafeIp() {
		return this.clientSafeIp;
	}

	public String getPhone() {
		return this.phone;
	}

	public String getAddress() {
		return this.address;
	}

	public List<BankAccount> getBankAccounts() {
		return this.bankAccounts;
	}

	public boolean isDisabled() {
		return this.disabled;
	}

	public Role getRole() {
		return this.role;
	}
}
