package com.abdelali.ebankservice.service;

import com.abdelali.ebankservice.entities.BankAccount;
import java.util.Date;
import java.util.UUID;
import java.util.Optional;
import java.util.List;
import com.abdelali.ebankservice.repository.BankAccountRepository;

import com.abdelali.ebankservice.model.Customer;

import org.springframework.stereotype.Service;
import com.abdelali.ebankservice.clients.CustomerRestClient;
import com.abdelali.ebankservice.model.Customer;


@Service
public class EBankService {
    private final BankAccountRepository bankAccountRepository;
    private final CustomerRestClient customerRestClient;

    public EBankService(BankAccountRepository bankAccountRepository, CustomerRestClient customerRestClient) {
        this.bankAccountRepository = bankAccountRepository;
        this.customerRestClient = customerRestClient;
    }

    public List<BankAccount> getAllAccounts() {
        return bankAccountRepository.findAll();
    }

    public Optional<BankAccount> getAccountById(String id) {
        return bankAccountRepository.findById(id)
                .map(account -> {
                    Customer customer = customerRestClient
                            .getCustomerById(account.getCustomerId());

                    account.setCustomer(customer);

                    return account;
                });
    }

    public List<BankAccount> getAccountsByCustomerId(Long customerId) {
        return bankAccountRepository.findByCustomerId(customerId);
    }

    public BankAccount createAccount(BankAccount account) {
        if ( account . getBalance () == null 
            || account . getBalance () < 0 
            || ! Double . isFinite ( account . getBalance ())) {

            throw new IllegalArgumentException (
            "Account balance must be a valid non-negative number" );
        }

        if (account.getCustomerId() == null) {
            throw new IllegalArgumentException("Customer ID is required");
        }

        if (account.getType() == null) {
            throw new IllegalArgumentException("Account type must be either SAVINGS or CURRENT");
        }

        account.setCreatedAt(new Date());
        account.setId(UUID.randomUUID().toString());

        return bankAccountRepository.save(account);
    }
}
