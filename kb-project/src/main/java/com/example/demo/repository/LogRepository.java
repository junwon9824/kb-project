package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.*;
import com.example.demo.entity.Log;
import com.example.demo.entity.User;

@Repository
public interface LogRepository extends JpaRepository<Log, Long> {

    List<Log> findByUserAndRecipientBankNumber(User user, String recipientBankNumber);
    // List<Log> findByUserAndBankNumber(User user, String bankNumber);
}
