package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.BankAccount;
import com.example.demo.entity.User;

import javax.persistence.LockModeType;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {
	void deleteByAccountNumber(String accountnumber);

	BankAccount findByAccountNumber(String accountnumber);

	List<BankAccount> findAllByUserId(Long userid);

//	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT b FROM BankAccount b WHERE b.accountNumber = :accountNumber")
	BankAccount findByAccountNumberWithLock(@Param("accountNumber") String accountNumber);


}
