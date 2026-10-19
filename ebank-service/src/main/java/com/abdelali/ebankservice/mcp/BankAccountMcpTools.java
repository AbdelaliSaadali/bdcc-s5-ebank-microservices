package com.abdelali.ebankservice.mcp;

import com.abdelali.ebankservice.entities.BankAccount;
import com.abdelali.ebankservice.service.EBankService;

import org.springaicommunity.mcp.annotation.McpTool;
import org.springaicommunity.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BankAccountMcpTools {

    private final EBankService eBankService;

    public BankAccountMcpTools(EBankService eBankService) {
        this.eBankService = eBankService;
    }

    @McpTool(
        name = "list_accounts",
        description = "Retrieve all bank accounts in the E-Bank system"
    )
    public List<BankAccount> listAccounts() {
        return eBankService.getAllAccounts();
    }

    @McpTool(
        name = "get_account_by_id",
        description = "Retrieve a bank account using its UUID identifier"
    )
    public BankAccount getAccountById(
        @McpToolParam(
            description = "The UUID identifier of the bank account",
            required = true
        )
        String accountId
    ) {
        return eBankService.getAccountById(accountId)
            .orElseThrow(() ->
                new IllegalArgumentException(
                    "Bank account not found: " + accountId
                )
            );
    }

    @McpTool(
        name = "list_accounts_by_customer",
        description = "Retrieve all bank accounts belonging to a customer"
    )
    public List<BankAccount> listAccountsByCustomer(
        @McpToolParam(
            description = "The numeric identifier of the customer",
            required = true
        )
        Long customerId
    ) {
        return eBankService.getAccountsByCustomerId(customerId);
    }
}