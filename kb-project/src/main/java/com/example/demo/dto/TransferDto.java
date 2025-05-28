package com.example.demo.dto;

import com.example.demo.entity.Log;
import com.example.demo.entity.BankAccount;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class TransferDto {

    private BankAccount senderAccount;      // 송금자 계좌
    private BankAccount recipientAccount;   // 수신자 계좌

    private String recipient_name;
    private String recipient_banknumber;
    private String category;
    private String sender_banknumber;
    private String sender_name;
    private String account_password;

    private Long amount;

    public Log toEntity() {
        Log log = new Log();
        log.sender = senderAccount.getUser();
        log.recipient = recipientAccount.getUser();
        log.amount = amount;
        log.recipientName = recipient_name;
        log.category = category;
        log.senderName = sender_name;
        return log;
    }

}
