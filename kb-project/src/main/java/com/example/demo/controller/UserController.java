package com.example.demo.controller;

import java.util.List;
import java.util.Optional;

import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.Login;
import com.example.demo.dto.TransferDto;
import com.example.demo.dto.UserDto;
import com.example.demo.entity.BankAccount;
import com.example.demo.entity.User;
import com.example.demo.repository.BankAccountRepository;
import com.example.demo.service.BankAccountService;
import com.example.demo.service.LogService;
import com.example.demo.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@RestController
public class UserController {

	@Autowired
	private UserService userService;

	@Autowired
	private LogService logService;

	@Autowired
	private BankAccountService bankaccountservice;

	@Autowired
	private BankAccountRepository bankAccountRepository;
    @Autowired
    private UserRepository userRepository;

	@GetMapping("/users/{id}")
	public User getUser(@PathVariable("id") Long id) {
		return userService.getUserById(id).orElse(null);
	}

	@GetMapping("/users")
	public List<User> getAllUsers() {
		return userService.getAllUsers();
	}

	@PostMapping("/users")
	public User createUser(@RequestBody UserDto userDto, HttpServletRequest request) {
		userDto.setClientSafeIp(request.getRemoteAddr());
		return userService.createUser(userDto);
	}

	@PutMapping("/users/{id}")
	public User updateUser(@PathVariable("id") Long id, @RequestBody UserDto userDto) {
		return userService.updateUser(id, userDto.toEntity());
	}

	@DeleteMapping("/users/{id}")
	public void deleteUser(@PathVariable("id") Long id) {
		userService.deleteUser(id);
	}

//	@PostMapping("/users/login")
//	public ResponseEntity<?> login(@RequestBody Login login, HttpSession session) {
//		User user = userService.getUserByUserId(login.getUserid());
//		if (user != null && user.getPassword().equals(login.getPassword())) {
//			session.setAttribute("user", user);
//			return ResponseEntity.ok(user);
//		} else {
//			return ResponseEntity.status(401).body("Invalid credentials");
//		}
//	}

	@PostMapping("/users/logout")
	public ResponseEntity<?> logout(HttpSession session) {
		session.invalidate();
		return ResponseEntity.ok("Logged out");
	}

	@GetMapping("/users/me")
	public User getCurrentUser(HttpSession session) {
		return (User) session.getAttribute("user");
	}

	// 송금 등 기타 기능은 별도 RESTful 엔드포인트로 분리 필요
}
