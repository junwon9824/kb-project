package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Log extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_account_id", nullable = false)
    private BankAccount senderAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_account_id", nullable = false)
    private BankAccount recipientAccount;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private Long amount;

    private String recipientName;
    private String senderName;

    public String getRecipientBankNumber() {
        return recipientAccount != null ? recipientAccount.getAccountNumber() : null;
    }

    public String getSenderBankNumber() {
        return senderAccount != null ? senderAccount.getAccountNumber() : null;
    }
}