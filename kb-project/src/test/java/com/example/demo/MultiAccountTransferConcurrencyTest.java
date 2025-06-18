package com.example.demo;

import com.example.demo.dto.*;
import com.example.demo.entity.*;
import com.example.demo.repository.*;
import com.example.demo.service.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;

import java.util.List;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
//@Commit // 실제 DB에 반영하려면 주석 해제, 트랜잭션 롤백을 원하면 @Transactional 추가
public class MultiAccountTransferConcurrencyTest {

    @Autowired
    private BankAccountService transferService;

    @Autowired
    private BankAccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LogService logService; // 로그 서비스

    @Autowired
    private LogRepository logRepository; // 로그 레포지토리

    private final String toAccountNumber = "123-456-789";
    private final int accountCount = 100; // 1~100까지
    private final Long transferAmount = 1000L;

    @BeforeEach
    void setUp() {
        // 수신 계좌 초기화 (잔액 0)
        BankAccount toAccount = accountRepository.findByAccountNumber(toAccountNumber);
        if (toAccount == null) {
            toAccount = new BankAccount();
            toAccount.setAccountNumber(toAccountNumber);
        }
        toAccount.setBalance(0L);

        // 수신자 User 생성 및 연결
        String recipientUserId = "junho1131";
        User recipientUser = userRepository.findByuserid(recipientUserId);

        if (recipientUser == null) {
            recipientUser = new User();
            recipientUser.setUserid(recipientUserId);
            recipientUser.setUsername("받는사람");
            userRepository.save(recipientUser);
        }

        toAccount.setUser(recipientUser);
        accountRepository.save(toAccount);

        // 보내는 계좌 1~100까지 초기화 (충분한 잔액)
        for (int i = 1; i <= accountCount; i++) {
            String fromAccountNumber = String.format("accId%d", i);
            BankAccount fromAccount = accountRepository.findByAccountNumber(fromAccountNumber);
            if (fromAccount == null) {
                fromAccount = new BankAccount();
                fromAccount.setAccountNumber(fromAccountNumber);
            }
            fromAccount.setBalance(1000000L);

            // 보내는 User 생성 및 연결
            String senderUserId = String.format("userid%d", i);
            User sender = userRepository.findByuserid(senderUserId);
            if (sender == null) {
                sender = new User();
                sender.setUserid(senderUserId);
                sender.setUsername("보내는사람" + i);
                userRepository.save(sender);
            }
            fromAccount.setUser(sender);
            accountRepository.save(fromAccount);
        }
    }

    @Test
    void 여러계좌_동시_송금_정합성_테스트() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(accountCount);
        CountDownLatch latch = new CountDownLatch(accountCount);

        // 1. 1~100까지 계좌에서 동시에 송금
        for (int i = 1; i <= accountCount; i++) {
            String str = String.format("userid%d", i);


            String fromAccountNumber = String.format("accId%d", i);
            TransferDto dto = new TransferDto();
            dto.setSender_banknumber(fromAccountNumber);
            dto.setRecipient_banknumber(toAccountNumber);
            dto.setAmount(transferAmount);
            dto.setSenderAccount(accountRepository.findByAccountNumber(fromAccountNumber));
            dto.setRecipientAccount(accountRepository.findByAccountNumber(toAccountNumber));
            dto.setCategory("송금");
            dto.setSender_name("user" + i);
            dto.setRecipient_name("정준호");
            dto.setSenderUserId(str );
            dto.setRecipientUserId("junho1131");

            User sender = userRepository.findByuserid(str);

            executorService.execute(() -> {
                try {
                    transferService.transferToUser(dto, sender);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        // 2. 최종 잔액 검증
        BankAccount toAccount = accountRepository.findByAccountNumber(toAccountNumber);
        assertThat(toAccount.getBalance()).isEqualTo(transferAmount * accountCount);


        String str= "junho1131";
        // 4. 캐시(Redis)를 사용한 로그 조회 및 개수 검증
        List<LogDto> logs = logService.getLogs(str, toAccountNumber);
        assertThat(logs.size()).isEqualTo(accountCount);


    }
}
