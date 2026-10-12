package com.abdelali.ebankservice.web;

import java.util.List;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ExceptionHandler;

import org.springframework.http.ResponseEntity;
import java.net.URI;

import com.abdelali.ebankservice.entities.BankAccount;

import com.abdelali.ebankservice.service.EBankService;

@RestController
@RequestMapping("/accounts")
public class BankAccountRestController {
    private final EBankService eBankService;

    public BankAccountRestController(EBankService eBankService) {
        this.eBankService = eBankService;
    }

    @GetMapping
    public List<BankAccount> getAllAccounts() {
        return eBankService.getAllAccounts();
    }

    @GetMapping("/{id}")
    public ResponseEntity<BankAccount> getAccountById(@PathVariable("id") String id) {
        return eBankService.getAccountById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/customer/{customerId}")
    public List<BankAccount> getAccountsByCustomerId(@PathVariable("customerId") Long customerId) {
        return eBankService.getAccountsByCustomerId(customerId);
    }

    @PostMapping
    public ResponseEntity<BankAccount> createAccount(@RequestBody BankAccount bankAccount) {
        BankAccount savedAccount = eBankService.createAccount(bankAccount);
        URI location = URI.create("/accounts/" + savedAccount.getId());
        return ResponseEntity.created(location).body(savedAccount);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleInvalidAccount(IllegalArgumentException exception) {
        return ResponseEntity.badRequest().body(exception.getMessage());
    }
}
