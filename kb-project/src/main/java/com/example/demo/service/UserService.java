package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dto.Login;
import com.example.demo.dto.UserDto;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;

@Service
@Slf4j
public class UserService {

	private final UserRepository userRepository;

	@Autowired
	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public User createUser(UserDto userDto) {

		User user2 = userDto.toEntity();
		log.info("user2"+user2.toString());
		try {
			User user = userRepository.save(user2);
			log.info("userrrr" + user.getId());
			return user;
		} catch (Exception e) {
			log.error("Error saving user: " + e.getMessage());
			throw new RuntimeException("Error saving user", e);
		}

	}

	public User updateUser(Long id, User user) {
		Optional<User> optionalUser = userRepository.findById(id);

		if (optionalUser.isPresent()) {
			User user1 = optionalUser.get();
			user1.setUsername(user.getUsername());
			user1.setUserid(user.getUserid());
			user1.setPassword(user.getPassword());
			user1.setPhone(user.getPhone());
			user1.setAddress(user.getAddress());

			return userRepository.save(user1);
		}

		return null;
	}

	public void deleteUser(Long id) {
		userRepository.deleteById(id);
	}

	public List<User> getAllUsers() {
		return userRepository.findAll();
	}

	public Optional<User> getUserById(Long id) {
		return userRepository.findById(id);
	}

	public User getUserByUserId(String userid) {
		User user = userRepository.findByuserid(userid);
		return user;

	}

	public User getUserByUsername(String username) {
		User user = userRepository.findByUsername(username);
		return user;

	}

	// User Ip get
	public String getClientIp(HttpServletRequest request) {
		String clientIp = request.getHeader("X-Forwarded-For");
		if (clientIp == null || clientIp.length() == 0 || "unknown".equalsIgnoreCase(clientIp)) {
			clientIp = request.getHeader("Proxy-Client-IP");
		}
		if (clientIp == null || clientIp.length() == 0 || "unknown".equalsIgnoreCase(clientIp)) {
			clientIp = request.getHeader("WL-Proxy-Client-IP");
		}
		if (clientIp == null || clientIp.length() == 0 || "unknown".equalsIgnoreCase(clientIp)) {
			clientIp = request.getRemoteAddr();
		}
		return clientIp;
	}

}
