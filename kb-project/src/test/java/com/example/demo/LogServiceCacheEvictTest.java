package com.example.demo;

import com.example.demo.dto.LogDto;
import com.example.demo.dto.TransferDto;
import com.example.demo.entity.BankAccount;
import com.example.demo.entity.Log;
import com.example.demo.entity.User;
import com.example.demo.repository.BankAccountRepository;
import com.example.demo.repository.LogRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.BankAccountService;
import com.example.demo.service.LogService;
import com.example.demo.service.UserService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.cache.CacheManager;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class LogServiceCacheEvictTest {

    @Autowired
    private LogService logService;

    @Autowired
    private LogRepository logRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private CacheManager cacheManager;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BankAccountRepository bankAccountRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private BankAccountService bankAccountService;

    @BeforeEach
    void clearRedis() {
        // 테스트 전마다 전체 Redis DB를 초기화
        redisTemplate.getConnectionFactory().getConnection().flushDb();
        System.out.println("[디버그] Redis DB 초기화 완료");
    }

    @Test
    @Transactional
    void save_호출시_캐시가_무효화되는지_확인() {
        // given
        String userId = "junwon9824";
        String bankNumber = "9824";

        // 1. 테스트용 User, Account, Log 객체 생성
        User senderUser = userRepository.findByuserid("junwon9824").orElseThrow(()->new NoSuchElementException("유저가 존재하지 " +
                "않습니다"+ userId));
        BankAccount senderAccount = bankAccountRepository.findByAccountNumber("9824");
        User recipientUser = userRepository.findByuserid("junho1131").orElseThrow(()->new NoSuchElementException("유저가 존재하지 " +
                "않습니다"+ userId));
        BankAccount recipientAccount = bankAccountRepository.findByAccountNumber("1131");

        System.out.println("[디버그] senderUser: " + senderUser);
        System.out.println("[디버그] senderAccount: " + senderAccount);
        System.out.println("[디버그] recipientUser: " + recipientUser.toString());
        System.out.println("[디버그] recipientAccount: " + recipientAccount);

        TransferDto transferDto = TransferDto.builder()
                .amount(1L)
                .category("'송금")
                .recipient_banknumber(recipientAccount.getAccountNumber())
                .recipient_name(recipientUser.getUsername())
                .sender_banknumber(senderAccount.getAccountNumber())
                .sender_name(senderUser.getUsername())
                .recipientAccount(recipientAccount)
                .senderAccount(senderAccount)
                .account_password(bankNumber)
                .build();

        System.out.println("[디버그] transferDto: " + transferDto);

        // 2. 캐시에 데이터 저장 (getLogs 호출)
        List<LogDto> firstCall = logService.getLogs(userId, bankNumber);
        System.out.println("[디버그] getLogs 호출 후 캐시 저장됨");

        // 3. 캐시가 정상적으로 저장되었는지 확인
        Object cached = redisTemplate.opsForValue().get(userId + "-" + bankNumber);
        System.out.println("[디버그] 캐시 저장 상태: " + cached);
        assertThat(cached).isNotNull();

        // 4. 로그 저장 (save 호출)
        bankAccountService.transferToUser(transferDto, senderUser);
        System.out.println("[디버그] bankAccountService.transferToUser 호출 완료");

        // 5. 캐시가 무효화(삭제)되었는지 확인
        Object afterEvict = redisTemplate.opsForValue().get(userId + "-" + bankNumber);
        System.out.println("[디버그] 캐시 삭제 후 상태: " + afterEvict);
        assertThat(afterEvict).isNull();

    }
}
