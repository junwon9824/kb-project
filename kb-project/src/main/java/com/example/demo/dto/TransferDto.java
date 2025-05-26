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
        log.setSenderAccount(senderAccount);
        log.setRecipientAccount(recipientAccount);
        log.setAmount(amount);
        log.setRecipientName(recipient_name);
        log.setCategory(category);
        log.setSenderName(sender_name);
        return log;
    }

}
