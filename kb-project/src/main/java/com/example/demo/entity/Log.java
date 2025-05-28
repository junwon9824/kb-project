package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

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
    @JoinColumn(name = "sender_account_id")
    private BankAccount senderAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_account_id")
    private BankAccount recipientAccount;

    @Column(nullable = false)
    public String category;

    @Column(nullable = false)
    public Long amount;

    public String senderBankNumber;
    public String recipientBankNumber;

    public String recipientName;
    public String senderName;

    public void setAmount(Long amount) { this.amount = amount; }
    public void setRecipientName(String recipientName) { this.recipientName = recipientName; }
    public void setCategory(String category) { this.category = category; }
    public void setSenderName(String senderName) { this.senderName = senderName; }
    public void setSenderBankNumber(String senderBankNumber) { this.senderBankNumber = senderBankNumber; }
    public void setRecipientBankNumber(String recipientBankNumber) { this.recipientBankNumber = recipientBankNumber; }
}
