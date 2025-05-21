package com.example.demo.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import com.example.demo.dto.LogDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Service;

import com.example.demo.dto.BankAccountDto;
import com.example.demo.dto.TransferDto;
import com.example.demo.entity.BankAccount;
import com.example.demo.entity.Log;
import com.example.demo.entity.User;
import com.example.demo.repository.LogRepository;

@EnableCaching
@Service
public class LogService {

    private final LogRepository logRepository;
    private final UserService userService;
//	private final BankAccountService bankAccountService;

    @Autowired
    public LogService(LogRepository logRepository, UserService userService) {
        this.logRepository = logRepository;
        this.userService = userService;
//		this.bankAccountService = bankAccountService;
//		this.bankAccountRepository = bankAccountRepository;
    }

//	public void save(Log logentity) {
//
//		logRepository.save(logentity); // 계좌 내역 저장
//	}


    @CacheEvict(value = "logCache", key = "#logentity.user.userid + '-' + #logentity.user.id")
    public void save(Log logentity) {
        logRepository.save(logentity); // 계좌 내역 저장
    }


//	public List<Log> getlogs(User user, String mybanknumber) {
//		List<Log> logs = logRepository.findAll();
//		List<Log> logsList = new ArrayList<Log>();
//		String userid = user.getUserid();
//		for (Log log : logs) { //
//			if (log.getUser().getUserid().equals(userid) && (log.getRecipient_banknumber().equals(mybanknumber)
//					|| log.getSender_banknumber().equals(mybanknumber))) {
//
//				logsList.add(log);
//			}
//
//		}
//
//		return logsList;
//	}




    public List<LogDto> getlogswithoutcache(User user, String mybanknumber) {
        // 모든 로그를 가져옵니다.
        List<Log> logs = logRepository.findAll();
        String userid = user.getUserid();

        // 조건에 맞는 로그를 필터링합니다.
        List<Log> filteredLogs = logs.stream().filter(log -> log.getUser().getUserid().equals(userid)) // 사용자 아이디가 일치하는
                // 경우
                .filter(log -> log.getRecipient_banknumber().equals(mybanknumber)
                        || log.getSender_banknumber().equals(mybanknumber)) // 계좌번호가 일치하는 경우
                .collect(Collectors.toList());

        // createdDate 필드를 기준으로 내림차순으로 정렬합니다.
        Collections.sort(filteredLogs, Comparator.comparing(Log::getCreatedDate).reversed());

        List<LogDto> Logs = filteredLogs.stream().map(log ->
        {
            return LogDto.builder().
                    amount(log.getAmount())
                    .recipient_banknumber(log.getRecipient_banknumber())
                    .category(log.getCategory())
                    .sender_banknumber(log.getSender_banknumber())
                    .recipient_name(log.getRecipient_name())
                    .sender_name(log.getSender_name())
                    .createdDate(log.getCreatedDate())
                    .build();

        }).collect(Collectors.toList());

        // 정렬된 로그 리스트를 반환합니다.
        return Logs;
    }



    @Cacheable(value = "logCache", key = "#user.userid + '-' + #mybanknumber")
    public List<LogDto> getlogs(User user, String mybanknumber) {
        // 모든 로그를 가져옵니다.
        List<Log> logs = logRepository.findAll();
        String userid = user.getUserid();

        // 조건에 맞는 로그를 필터링합니다.
        List<Log> filteredLogs = logs.stream().filter(log -> log.getUser().getUserid().equals(userid)) // 사용자 아이디가 일치하는
                // 경우
                .filter(log -> log.getRecipient_banknumber().equals(mybanknumber)
                        || log.getSender_banknumber().equals(mybanknumber)) // 계좌번호가 일치하는 경우
                .collect(Collectors.toList());

        // createdDate 필드를 기준으로 내림차순으로 정렬합니다.
        Collections.sort(filteredLogs, Comparator.comparing(Log::getCreatedDate).reversed());

        List<LogDto> Logs = filteredLogs.stream().map(log ->
        {
            return LogDto.builder().
                    amount(log.getAmount())
                    .recipient_banknumber(log.getRecipient_banknumber())
                    .category(log.getCategory())
                    .sender_banknumber(log.getSender_banknumber())
                    .recipient_name(log.getRecipient_name())
                    .sender_name(log.getSender_name())
                    .createdDate(log.getCreatedDate())
                    .build();

        }).collect(Collectors.toList());

        // 정렬된 로그 리스트를 반환합니다.
        return Logs;
    }


//    @Cacheable(value = "logCache", key = "#user.userid + '-' + #mybanknumber")
//    public List<LogDto> getlogs(User user, String mybanknumber) {
//        String userid = user.getUserid();
//
//        // DB에서 필터링된 로그만 가져옴
//        List<Log> logs = logRepository.findByUserIdAndBanknumber(userid, mybanknumber);
//
//        // createdDate 기준 내림차순 정렬
//        logs.sort(Comparator.comparing(Log::getCreatedDate).reversed());
//
//        // LogDto로 변환
//        return logs.stream().map(log -> LogDto.builder()
//                        .amount(log.getAmount())
//                        .recipient_banknumber(log.getRecipient_banknumber())
//                        .category(log.getCategory())
//                        .sender_banknumber(log.getSender_banknumber())
//                        .recipient_name(log.getRecipient_name())
//                        .sender_name(log.getSender_name())
//                        .createdDate(log.getCreatedDate())
//                        .build())
//                .collect(Collectors.toList());
//    }



    // Add other methods as needed

}