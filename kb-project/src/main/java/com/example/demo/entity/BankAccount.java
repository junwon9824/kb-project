package com.example.demo.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

import com.example.demo.dto.BankAccountDto;

import lombok.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
// 순환참조로 인한 toString 무한루프 방지 위해 연관관계 필드는 제외
@ToString(exclude = {"user", "bank", "sentLogs", "receivedLogs"})
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
				.user(this.user)
				.bank(this.bank)
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
}
