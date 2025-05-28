package com.example.demo.entity;

import java.util.Date;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "savings_account")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SavingBankAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long amount;

    private String accountNumber;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "bank_id")
    private Bank bank;

    private boolean mainAccount;

    private int term; // 적금 계약 기간
    private double interestRate; // 이자율
    private Date maturityDate; // 만기일

}
