package com.example.demo.dto;

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

}
