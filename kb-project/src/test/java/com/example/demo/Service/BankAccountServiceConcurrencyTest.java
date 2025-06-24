package com.example.demo.Service;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.entity.BankAccount;
import com.example.demo.entity.User;
import com.example.demo.repository.BankAccountRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.BankAccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;

import java.util.NoSuchElementException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootTest
@Commit
class BankAccountServiceConcurrencyTest {

    @Autowired
    private BankAccountService bankAccountService;

    @Autowired
    private BankAccountRepository bankAccountRepository;

    private String testAccountNumber = "123-456-789";
    private Long initialAmount = 0L;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        // 테스트 User가 없으면 생성
        User user = userRepository.findByuserid("junwon9824")
                .orElseThrow(() -> new NoSuchElementException("해당 사용자를 찾을 수 없습니다: " ));


//        if (user == null) {
//            user = new User();
//            user.setUserid("junwon9824");
//            user.setUsername("테스트유저");
//            // 필요한 필드 추가 세팅
//            userRepository.save(user);
//        }

        // 테스트 계좌를 생성하고 항상 User를 연결
        BankAccount account = bankAccountRepository.findByAccountNumber(testAccountNumber);
        if (account == null) {
            account = new BankAccount();
            account.setAccountNumber(testAccountNumber);
            account.setAmount(initialAmount);
            account.setUser(user); // User 연결
            bankAccountRepository.save(account);
        } else {
            account.setAmount(initialAmount);
            account.setUser(user); // 이미 존재하는 계좌에도 User 연결 보장
            bankAccountRepository.save(account);
        }
    }

    @Test
    void 동시_입금_정합성_테스트() throws InterruptedException {
        int threadCount = 100; // 동시에 입금할 스레드 수
        Long depositAmount = 1000L; // 각 스레드가 입금할 금액
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // 10개의 스레드가 동시에 같은 계좌에 입금
        for (int i = 0; i < threadCount; i++) {
            executorService.execute(() -> {
                try {
                    bankAccountService.depositToAccount(testAccountNumber, depositAmount, "테스터");
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(); // 모든 스레드 완료까지 대기

        // 최종 잔액 검증
        BankAccount resultAccount = bankAccountRepository.findByAccountNumber(testAccountNumber);
        assertThat(resultAccount.getAmount()).isEqualTo(initialAmount + depositAmount * threadCount);
    }
}
