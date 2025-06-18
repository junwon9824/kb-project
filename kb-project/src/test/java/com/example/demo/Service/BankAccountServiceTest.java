package com.example.demo.Service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.TransferDto;
import com.example.demo.entity.BankAccount;
import com.example.demo.entity.Log;
import com.example.demo.entity.User;
import com.example.demo.repository.BankAccountRepository;
import com.example.demo.repository.LogRepository;
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
    private LogRepository logRepository;

    @Autowired
    private BankService bankService;

    @Autowired
    private UserService userService;

    @Autowired
    private BankAccountService bankAccountService;

    @Autowired
    private UserRepository userRepository;

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

        // [추가] Log 테이블 직접 조회
        List<Log> logs = logRepository.findAll();
        System.out.println("Log count: " + logs.size());
        logs.forEach(System.out::println);
    }

    @Test
    void depositToAccount() {
    }


}