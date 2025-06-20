package com.example.demo.controller;

import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import com.example.demo.repository.BankAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.BookMarkDto;
import com.example.demo.dto.TransferDto;
import com.example.demo.entity.BankAccount;
import com.example.demo.entity.BookMark;
import com.example.demo.entity.User;
import com.example.demo.service.BankAccountService;
import com.example.demo.service.BookMarkService;
import com.example.demo.service.UserService;

@RestController
@RequestMapping("/bookmarks")
public class BookMarkController {

	@Autowired
	private BookMarkService bookMarkService;

	@Autowired
	private UserService userService;
	@Autowired
	private BankAccountService bankaccountservice;
	@Autowired
	private BankAccountRepository bankAccountRepository;

	@GetMapping
	public List<BookMark> getUserBookMarks(HttpServletRequest request, HttpSession session) {
		User user = (User) session.getAttribute("user");
		return bookMarkService.getUserAllBookmarks(user.getUserid(), user);
	}

	@PostMapping
	public BookMark createBookMark(@RequestBody BookMarkDto bookMark, HttpSession session) {
		User user = (User) session.getAttribute("user");
		BookMark bookMark2 = bookMark.toEntity();
		if (userService.getUserByUsername(bookMark2.getBookMarkName()) != null) {
			bookMarkService.createBookMark(bookMark);
			return bookMark2;
		}
		return null;
	}

	@PutMapping("/{id}")
	public BookMark updateBookMark(@PathVariable("id") Long id, @RequestBody BookMarkDto updatedBookMark) {
		return bookMarkService.updateBookMark(id, updatedBookMark);
	}

	@DeleteMapping("/{id}")
	public void deleteBookMark(@PathVariable("id") Long id) {
		bookMarkService.deleteBookMark(id);
	}

	// 즐겨찾기 계좌로 송금 폼 데이터 반환 (REST)
	@GetMapping("/transferbookmark/{recepientAccountNumber}")
	public TransferDto getTransferBookmarkInfo(@PathVariable("recepientAccountNumber") String recepientAccountNumber, HttpSession session) {
		User user = (User) session.getAttribute("user");
		TransferDto transferDto = new TransferDto();
		transferDto.setRecipient_banknumber(recepientAccountNumber);
		BankAccount bankAccount = bankaccountservice.getBankAccountByAccountnumber(recepientAccountNumber);
		transferDto.setRecipient_name(bankAccount.getUser().getUsername());
		return transferDto;
	}

	// 즐겨찾기 계좌로 송금 (REST)
	@PostMapping("/transferbookmark")
	public ResponseEntity<?> transferBookmark(@RequestBody TransferDto log, HttpSession session) {
		User user = (User) session.getAttribute("user");
		if (!log.getAccount_password().equals(user.getAccount_password())) {
			return ResponseEntity.status(401).body("비밀번호 오류");
		}
		bankaccountservice.transferToUser(log, user);
		return ResponseEntity.ok("송금 완료");
	}
}