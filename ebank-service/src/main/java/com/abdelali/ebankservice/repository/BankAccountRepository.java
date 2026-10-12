package com.abdelali.ebankservice.repository;

import com.abdelali.ebankservice.entities.BankAccount;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BankAccountRepository extends JpaRepository<BankAccount, String> {
    List<BankAccount> findByCustomerId(Long customerId);
}
