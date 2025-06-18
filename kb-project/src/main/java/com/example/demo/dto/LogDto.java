package com.example.demo.dto;

import com.example.demo.entity.Log;
import com.example.demo.entity.User;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogDto {

//	private User user;

	private String recipient_name;
	private Date createdDate;
	private String recipient_banknumber;
	private String category;
	private String sender_banknumber;
	private String sender_name;
	private Long amount;

	public static LogDto fromEntity(Log log) {
		return LogDto.builder()
				.recipient_name(log.getRecipientName())
				.createdDate(log.getCreatedDate())
				.recipient_banknumber(log.getRecipientBankNumber())
				.category(log.getCategory())
				.sender_banknumber(log.getSenderBankNumber())
				.sender_name(log.getSenderName())
				.amount(log.getAmount())
				.build();
	}

}
