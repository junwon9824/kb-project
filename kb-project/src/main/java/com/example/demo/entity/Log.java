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
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id", nullable = false)
    private User recipient;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private Long amount;

    private String senderBankNumber;
    private String recipientBankNumber;

    private String recipientName;
    private String senderName;

    public User getUser() {
        return sender;
    }

    public String getRecipientBankNumber() {
        return recipient.getBankAccounts().get(0).getAccountNumber();
    }

    public String getSenderBankNumber() {
        return sender.getBankAccounts().get(0).getAccountNumber();
    }

    public String getRecipientName() {
        return recipient.getUsername();
    }

    public String getSenderName() {
        return sender.getUsername();
    }

    public void setUser(User user) {
        this.sender = user;
    }

    public void setSenderBankNumber(String senderBankNumber) {
        this.senderBankNumber = senderBankNumber;
    }

    public void setRecipientBankNumber(String recipientBankNumber) {
        this.recipientBankNumber = recipientBankNumber;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }
}
