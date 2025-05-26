package com.example.demo.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.example.demo.dto.BookMarkDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookMark {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id; // 북마크 ID

	private String bookMarkName; // 북마크 이름
	private String bookMarkAccountNumber; // 계좌 번호
	private String bookMarkBankname; // 은행 이름

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user; // 사용자 정보

	/**
	 * BookMark 엔티티를 BookMarkDto로 변환합니다.
	 *
	 * @return BookMarkDto 객체
	 */
	public BookMarkDto toDto() {
		return BookMarkDto.builder()
				.bookMarkName(this.bookMarkName)
				.bookMarkAccountNumber(this.bookMarkAccountNumber)
				.bookMarkBankname(this.bookMarkBankname)
				.user(this.user) // 필요 시 UserDto로 변환
				.build();
	}
}