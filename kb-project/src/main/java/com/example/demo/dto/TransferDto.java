package com.example.demo.dto;

import com.example.demo.entity.Log;
import com.example.demo.entity.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
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

    private User user;

    private String recipient_name;
    private String recipient_banknumber;
    private String category;
    private String sender_banknumber;
    private String sender_name;
    private String account_password;
    private String senderBankNumber;

    private Long amount;

    public Log toEntity() {
        Log log = new Log();
        log.setUser(user);  // <-- Now works
        log.setAmount(amount);
        log.setSenderBankNumber(senderBankNumber);
        log.setRecipientBankNumber(recipient_banknumber);
        log.setRecipientName(recipient_name);
        log.setCategory(category);
        log.setSenderName(sender_name);
        return log;
    }

}
