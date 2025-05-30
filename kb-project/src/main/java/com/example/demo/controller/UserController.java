package com.example.demo.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.dto.Login;
import com.example.demo.dto.TransferDto;
import com.example.demo.dto.UserDto;
import com.example.demo.entity.BankAccount;
import com.example.demo.entity.User;
import com.example.demo.repository.BankAccountRepository;
import com.example.demo.service.BankAccountService;
import com.example.demo.service.LogService;
import com.example.demo.service.UserService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j

@Controller
public class UserController {

	@Autowired
	private UserService userService;

	@Autowired
	private LogService logService;

	@Autowired
	private BankAccountService bankaccountservice;

	@Autowired
	private BankAccountRepository bankAccountRepository;
	//
	// @GetMapping("/users")
	// public String getUsers(Model model, HttpServletRequest request) {
	//
	// HttpSession session = request.getSession();
	// User user = (User) session.getAttribute("user");
	//
	// List<User> userList = userService.getAllUsers();
	// model.addAttribute("users", userList);
	//
	// if (user.isDisabled()) {// 장애인
	//
	// return "user/list2";
	//
	// } else {// 비장애인
	// return "user/list";
	//
	// }
	//
	// }

	@GetMapping("/users/{id}")
	public String getUser(@PathVariable("id") Long id, Model model) {

		Optional<User> user = userService.getUserById(id);
		model.addAttribute("user", user);
		return "user/view";
	}

	@GetMapping("/users/new")
	public String createUserForm(Model model, HttpServletRequest request) {

		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");

		model.addAttribute("user", new User());

		if (user.isDisabled()) {// 장애인

			return "user/new2";

		} else {// 비장애인
			return "user/new";

		}

	}

	@PostMapping("/users/new")
	public String createUser(@Valid @ModelAttribute("user") UserDto userDto, BindingResult result,
			HttpServletRequest request) {
		System.out.println(" userDto.getUsername()" + userDto.getUsername());

		if (result.hasErrors()) {
			return "user/new";
		}
		String clientIp = userService.getClientIp(request);
		System.out.println("user Client ip : " + clientIp);
		System.out.println("user password : " + userDto.getPassword());
		System.out.println("user getUsername : " + userDto.getUsername());
		userDto.setClientSafeIp(clientIp);
		userService.createUser(userDto);
		return "redirect:/users/index";

	}

	@GetMapping("/users/edit/{id}")
	public String editUserForm(@PathVariable("id") Long id, Model model) {

		Optional<User> user = userService.getUserById(id);

		model.addAttribute("user", user.get());
		model.addAttribute("user.id", user.get().getId());

		if (user.get().isDisabled()) {// 장애인

			return "user/edit2";

		} else {// 비장애인
			return "user/edit";

		}

	}

	@PostMapping("/users/edit/{id}")
	public String editUser(@PathVariable("id") Long id, @Valid @ModelAttribute("user") User user,
			BindingResult result) {

		if (result.hasErrors()) {

			if (user.isDisabled()) {// 장애인

				return "user/edit2";

			} else {// 비장애인
				return "user/edit";

			}

		}

		userService.updateUser(id, user);
		return "redirect:/users";

	}

	@GetMapping("/users/delete/{id}")
	public String deleteUser(@PathVariable("id") Long id) {
		userService.deleteUser(id);
		return "redirect:/users";
	}

	@GetMapping("/users/index")
	public String mainpageForm(Model model, HttpServletRequest request) {

		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");

		model.addAttribute("login", new Login());
		model.addAttribute("user", new UserDto());

		System.out.println("userrrr" + model.getAttribute("user"));

		return "user/index";

	}
	//
	// @GetMapping("/users/index")
	// public String mainpageForm(Model model, HttpServletRequest request) {
	//
	// // Authorization 헤더에서 JWT 토큰 추출
	// String token = request.getHeader("Authorization");
	//
	// if (token != null && token.startsWith("Bearer ")) {
	// token = token.substring(7); // "Bearer "를 제외한 실제 토큰만 추출
	// }
	//
	// if (token != null) {
	// try {
	// // JWT 토큰 검증 및 사용자 정보 가져오기
	// Claims claims = Jwts.parser()
	// .setSigningKey("yourSecretKey") // JWT 생성 시 사용한 SecretKey
	// .parseClaimsJws(token)
	// .getBody();
	//
	// // 사용자 정보를 UserDto로 매핑 (예시: UserDto에 필요한 정보)
	// UserDto userDto = new UserDto();
	// userDto.setUsername(claims.getSubject());
	// // 필요한 다른 클레임들도 userDto에 설정
	//
	// // 모델에 userDto 추가
	// model.addAttribute("user", userDto);
	//
	// } catch (Exception e) {
	// // 토큰 검증 실패 시 처리 (예: 토큰 만료, 변조 등)
	// model.addAttribute("error", "Invalid or expired token");
	// }
	// }
	//
	// model.addAttribute("login", new Login());
	// return "user/index";
	// }

	@PostMapping("/users/index")
	public String mainpage(@ModelAttribute("login") Login login, RedirectAttributes redirectAttributes,
			HttpSession session, HttpServletResponse response) {

		User userByUserId = userService.getUserByUserId(login.getUserid());

		if (userByUserId != null && login.getPassword().equals(userByUserId.getPassword())) {
			session.setAttribute("user", userByUserId); // 세션에 사용자 정보 저장
			System.out.println("session saveeeee" + userByUserId.getUserid());

			if (userByUserId.isDisabled() == false) {
				log.info("not disabled");
				// 세션 쿠키 수동 설정
				Cookie cookie = new Cookie("JSESSIONID", session.getId());
				cookie.setPath("/"); // 모든 경로에 대해 유효
				cookie.setHttpOnly(true); // JavaScript에서 접근 불가
				cookie.setMaxAge(60 * 60 * 24); // 24시간 동안 유효 (단위는 초)
				response.addCookie(cookie); // 응답에 쿠키 추가
				System.out.println("세션 ID: " + session.getId());

				return "redirect:/users/main";
			} else {
				System.out.println("장애인");
				return "redirect:/users/main";
			}

		}

		else {
			redirectAttributes.addFlashAttribute("errorMessage", "회원정보 오류");
			System.out.println("회원정보오류" + userByUserId.getUserid() + " " + userByUserId.getUsername() + " "
					+ userByUserId.getPassword());
			return "redirect:/users/index";
		}

	}

	@GetMapping("/logout")
	public String logout(HttpServletRequest request) {
		// 세션 무효화
		HttpSession session = request.getSession();
		if (session != null) {
			System.out.println("session inval");
			session.invalidate();
		}
		// 로그인 페이지로 리다이렉트
		return "redirect:/users/index";
	}

	@GetMapping("users/main")
	public String main(HttpServletRequest request, Model model) {
		// 여기에서 필요한 데이터를 모델에 추가하면 됩니다.

		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");

		System.out.println("usercontroller" + user);
		String userid = user.getUserid();

		model.addAttribute("userid", userid);

		if (user.isDisabled()) {// 장애인

			return "user/main2";

		} else {// 비장애인
			return "user/main";

		}

	}

	@GetMapping("/transfer")
	public String transferform(HttpServletRequest request, Model model) {
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		Long id = user.getId();// 세션갱신이 안되기 땜에 갱신하기 위해서 유저 아이디를 받아오고 밑에 유저값을 다시 받아온다.
		Optional<User> user2 = userService.getUserById(id);
		Long userId = user.getId();
		System.out.println("유저 아이디" + userId);
		TransferDto transferDto = new TransferDto();
		List<BankAccount> bankAccounts = bankAccountRepository.findAllByUserId(userId);
		System.out.println(bankAccounts);

		model.addAttribute("Log", transferDto);
		log.info("logggggg", transferDto);
		model.addAttribute("bankAccounts", bankAccounts);
		model.addAttribute("user", user);
		if (user.isDisabled()) {// 장애인

			return "user/transfer2";

		} else {// 비장애인
			return "user/transfer";

		}
	}

	@PostMapping("/transfer")
	public String transfer(HttpSession session, @ModelAttribute("Log") TransferDto log, BindingResult result,
			RedirectAttributes redirectAttributes) {

		User user = (User) session.getAttribute("user");
		String userid = user.getUserid();
		System.out.println("beforeeeee log.tostringgggggg" + log.toString());

		log.setCategory("송금");
		log.setSender_name(user.getUsername());
		log.setSender_banknumber(log.getSender_banknumber()); // Add this line
		log.setRecipientAccount(log.getRecipientAccount()); // Add this line
		log.setAmount(log.getAmount()); // Add this line
		log.setRecipient_banknumber(log.getRecipient_banknumber()); // Add this line
		log.setRecipient_name(log.getRecipient_name());
		log.setRecipientAccount(bankAccountRepository.findByAccountNumber(log.getRecipient_banknumber()));
		log.setSenderAccount(bankAccountRepository.findByAccountNumber(log.getSender_banknumber()));

		System.out.println("log.tostringgggggg" + log.toString());
		System.out.println("flag");

		String account_password = log.getAccount_password();

		boolean iscorrect = verifypassword(user, account_password);

		if (!iscorrect) {
			result.rejectValue("account_password", "password.mismatch", "비밀번호가 틀렸습니다.");
			redirectAttributes.addFlashAttribute("errorMessage", "비밀번호 오류");

			System.out.println("계좌비밀번호가 일치하지않음.");
			return "redirect:/transfer";
		}

		else {
			System.out.println("계좌비밀번호가 일치.");
			System.out.println("log.sendername" + log.getSender_name());
			bankaccountservice.transferToUser(log, user);
			redirectAttributes.addFlashAttribute("successMessage", "송금이 완료되었습니다."); // 송금 완료 메시지를 추가합니다.
			return "redirect:/users/main";
		}

	}

	@PostMapping("/testtransfer")
	public String transfer(@RequestParam("userId") String userId,
			@ModelAttribute("Log") TransferDto log,
			BindingResult result,
			RedirectAttributes redirectAttributes) {

		// 세션 없이 userId로 직접 조회
		User user = userService.getUserByUserId(userId);
		if (user == null) {
			System.out.println("usernulllll  userId" + userId);
			redirectAttributes.addFlashAttribute("errorMessage", "유저를 찾을 수 없습니다.");
			return "redirect:/transfer";
		}

		log.setCategory("송금");
		log.setSender_name(user.getUsername());

		System.out.println("log.tostring" + log.toString());
		System.out.println("flag");

		String account_password = log.getAccount_password();

		boolean iscorrect = verifypassword(user, account_password);
		if (!iscorrect) {
			result.rejectValue("account_password", "password.mismatch", "비밀번호가 틀렸습니다.");
			redirectAttributes.addFlashAttribute("errorMessage", "비밀번호 오류");
			return "redirect:/transfer";
		}

		bankaccountservice.transferToUser(log, user);
		redirectAttributes.addFlashAttribute("successMessage", "송금이 완료되었습니다.");
		return "redirect:/users/main";
	}

	static boolean verifypassword(User user, String account_password) {

		if (account_password.equals(user.getAccount_password())) {

			return true;
		}

		return false;
	}

}
