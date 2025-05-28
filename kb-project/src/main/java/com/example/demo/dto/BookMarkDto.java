package com.example.demo.dto;

import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import com.example.demo.entity.BookMark;
import com.example.demo.entity.User;

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
public class BookMarkDto {

	private String bookMarkName;

	private String bookMarkAccountNumber;

	private String bookMarkBankname;

	private User user;

	public BookMark toEntity() {

		BookMark bookMark = BookMark.builder().
				bookMarkName(this.bookMarkName).
				bookMarkBankname(this.bookMarkBankname)
				.bookMarkAccountNumber(this.bookMarkAccountNumber).user(user).
				
				build();
		return bookMark;
	}

}
