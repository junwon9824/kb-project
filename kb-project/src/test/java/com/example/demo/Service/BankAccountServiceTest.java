package com.example.demo.Service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.TransferDto;
import com.example.demo.entity.BankAccount;
import com.example.demo.entity.User;
import com.example.demo.repository.BankAccountRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.BankAccountService;
import com.example.demo.service.BankService;
import com.example.demo.service.UserService;

@SpringBootTest
@Transactional
class BankAccountServiceTest {

    @Autowired
    private BankAccountRepository bankAccountRepository;

    @Autowired
    private BankService bankService;

    @Autowired
    private UserService userService;

    @Autowired
    private BankAccountService bankAccountService;

    @Autowired
    private UserRepository userRepository;

    // No need for MockitoAnnotations.openMocks(this) when using @SpringBootTest and
    // @Autowired

    // @Test
    // void testDatabaseStateAfterTransfer() {
    // // Arrange
    // String senderAccountNumber = "11";
    // String recipientAccountNumber = "1";
    // Long transferAmount = 2L;

    // // Ensure sender and recipient accounts exist in the database
    // User sender = new User();
    // sender.setUserid("junho1131");
    // sender.setUsername("정준호");
    // sender.setPassword("ho1131"); // Set a password to avoid null constraint
    // violation
    // userRepository.save(sender);

    // User recipient = new User();
    // recipient.setUserid("junwon9824");
    // recipient.setUsername("정준원");
    // recipient.setPassword("study100*"); // Set a password to avoid null
    // constraint violation
    // userRepository.save(recipient);

    // BankAccount senderAccount = new BankAccount();
    // senderAccount.setAccountNumber(senderAccountNumber);
    // senderAccount.setBalance(100L);
    // senderAccount.setUser(sender);
    // bankAccountRepository.save(senderAccount);

    // BankAccount recipientAccount = new BankAccount();
    // recipientAccount.setAccountNumber(recipientAccountNumber);
    // recipientAccount.setBalance(50L);
    // recipientAccount.setUser(recipient);
    // bankAccountRepository.save(recipientAccount);

    // Long initialSenderBalance = senderAccount.getBalance();
    // Long initialRecipientBalance = recipientAccount.getBalance();

    // TransferDto transferDto = TransferDto.builder()
    // .amount(transferAmount)
    // .sender_banknumber(senderAccountNumber)
    // .recipient_banknumber(recipientAccountNumber)
    // .sender_name(senderAccount.getUser().getUsername())
    // .recipient_name(recipientAccount.getUser().getUsername())
    // .build();

    // // Act
    // bankAccountService.transferToUser(transferDto, senderAccount.getUser());

    // // Assert
    // BankAccount updatedSenderAccount =
    // bankAccountRepository.findByAccountNumber(senderAccountNumber);
    // BankAccount updatedRecipientAccount =
    // bankAccountRepository.findByAccountNumber(recipientAccountNumber);

    // assertEquals(initialSenderBalance - transferAmount,
    // updatedSenderAccount.getBalance());
    // assertEquals(initialRecipientBalance + transferAmount,
    // updatedRecipientAccount.getBalance());
    // }

    // 초기 데이터 생성 함수
    // @BeforeEach
    // void initTestData() {
    // // 기존 데이터 삭제 (테스트 격리 보장)
    // // bankAccountRepository.deleteAll();
    // // userRepository.deleteAll();

    // // 유저 생성
    // User sender = new User();
    // sender.setUserid("junwon9824");
    // sender.setUsername("정준원");
    // sender.setPassword("1111");
    // userRepository.save(sender);

    // User recipient = new User();
    // recipient.setUserid("junho1131");
    // recipient.setUsername("정준호");
    // recipient.setPassword("1111");
    // userRepository.save(recipient);

    // // 계좌 생성
    // BankAccount senderAccount = new BankAccount();
    // senderAccount.setAccountNumber("9824");
    // senderAccount.setBalance(1111L);
    // senderAccount.setUser(sender);
    // bankAccountRepository.save(senderAccount);

    // BankAccount recipientAccount = new BankAccount();
    // recipientAccount.setAccountNumber("1131");
    // recipientAccount.setBalance(1111L);
    // recipientAccount.setUser(recipient);
    // bankAccountRepository.save(recipientAccount);
    // }

    @Test
    @Rollback(false)
    void testTransferUsingExistingDatabaseUsers() {
        // Arrange
        String senderAccountNumber = "9824"; // Replace with actual sender account number in DB
        String recipientAccountNumber = "1131"; // Replace with actual recipient account number in DB
        Long transferAmount = 2L;

        // Fetch sender and recipient from the database
        BankAccount senderAccount = bankAccountRepository.findByAccountNumber(senderAccountNumber);
        BankAccount recipientAccount = bankAccountRepository.findByAccountNumber(recipientAccountNumber);

        assertNotNull(senderAccount, "Sender account not found in the database");
        assertNotNull(recipientAccount, "Recipient account not found in the database");

        Long initialSenderBalance = senderAccount.getBalance();
        Long initialRecipientBalance = recipientAccount.getBalance();

        TransferDto transferDto = TransferDto.builder()
                .amount(transferAmount)
                .sender_banknumber(senderAccountNumber)
                .recipient_banknumber(recipientAccountNumber)
                .sender_name(senderAccount.getUser().getUsername())
                .recipient_name(recipientAccount.getUser().getUsername())
                // .user()
                .senderAccount(senderAccount)
                .recipientAccount(recipientAccount)
                .build();

        // Act
        bankAccountService.transferToUser(transferDto, senderAccount.getUser());

        // Assert
        BankAccount updatedSenderAccount = bankAccountRepository.findByAccountNumber(senderAccountNumber);
        BankAccount updatedRecipientAccount = bankAccountRepository.findByAccountNumber(recipientAccountNumber);

        assertEquals(initialSenderBalance - transferAmount, updatedSenderAccount.getBalance());
        assertEquals(initialRecipientBalance + transferAmount, updatedRecipientAccount.getBalance());
    }
}