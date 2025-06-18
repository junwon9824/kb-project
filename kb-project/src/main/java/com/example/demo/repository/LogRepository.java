package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.*;
import com.example.demo.entity.Log;
import com.example.demo.entity.User;

@Repository
public interface LogRepository extends JpaRepository<Log, Long> {

    // List<Log> findByUserAndRecipientBankNumber(User user, String recipientBankNumber);

    List<Log> findBySenderAccountAccountNumberOrRecipientAccountAccountNumber(String bankNumber, String bankNumber1);

    long countByRecipientBankNumber(String toAccountNumber);

    @Query("SELECT DISTINCT l FROM Log l " +
            "LEFT JOIN FETCH l.senderAccount sa " +
            "LEFT JOIN FETCH sa.user " +
            "LEFT JOIN FETCH l.recipientAccount ra " +
            "LEFT JOIN FETCH ra.user " +
            "WHERE sa.accountNumber = :bankNumber OR ra.accountNumber = :bankNumber")
    List<Log> findBySenderOrRecipientAccountNumberWithUser(String bankNumber);

}
