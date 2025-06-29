package com.example.demo.entity;

import java.util.ArrayList;
import java.util.List;

import com.example.demo.dto.BankAccountDto;
import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
// 순환참조로 인한 toString 무한루프 방지 위해 연관관계 필드는 제외
@ToString(exclude = { "user", "bank", "sentLogs", "receivedLogs" })
public class BankAccount extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// 잔액
	private Long amount;

	@Column(unique = true)
	private String accountNumber;

	// 계좌 소유자 (User)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	// 은행 정보
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "bank_id")
	private Bank bank;

	// 주 계좌 여부
	private boolean mainAccount;

	private String password;

	// 이 계좌에서 보낸 거래 내역
	@OneToMany(mappedBy = "senderAccount", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Log> sentLogs = new ArrayList<>();

	// 이 계좌로 받은 거래 내역
	@OneToMany(mappedBy = "recipientAccount", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Log> receivedLogs = new ArrayList<>();

	public BankAccountDto toDto() {
		return BankAccountDto.builder()
				.id(this.id)
				.accountNumber(this.accountNumber)
				.amount(this.amount)
				.bank(this.bank)
				.user(this.user)
				.mainAccount(this.mainAccount)
				.build();
	}

	// 잔액 세팅
	public void setBalance(Long amount) {
		this.amount = amount;
	}

	// 잔액 조회
	public Long getBalance() {
		return this.amount;
	}

	// 계좌번호 세팅
	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public User getUser() {
		return this.user;
	}

	// 수동 getter 메서드들 추가
	public Long getId() {
		return this.id;
	}

	public String getAccountNumber() {
		return this.accountNumber;
	}

	public Long getAmount() {
		return this.amount;
	}

	public boolean isMainAccount() {
		return this.mainAccount;
	}

	public Bank getBank() {
		return this.bank;
	}

	public String getPassword() {
		return this.password;
	}

	// 수동 setter 메서드들 추가
	public void setId(Long id) {
		this.id = id;
	}

	public void setAmount(Long amount) {
		this.amount = amount;
	}

	public void setMainAccount(boolean mainAccount) {
		this.mainAccount = mainAccount;
	}

	public void setBank(Bank bank) {
		this.bank = bank;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setUser(User user) {
		this.user = user;
	}
}
