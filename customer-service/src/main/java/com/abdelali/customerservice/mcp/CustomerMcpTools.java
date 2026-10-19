package com.abdelali.customerservice.mcp;

import com.abdelali.customerservice.entities.Customer;
import com.abdelali.customerservice.service.CustomerService;

import org.springaicommunity.mcp.annotation.McpTool;
import org.springaicommunity.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CustomerMcpTools {

    private final CustomerService customerService;

    public CustomerMcpTools(CustomerService customerService) {
        this.customerService = customerService;
    }

    @McpTool(
        name = "list_customers",
        description = "Retrieve all customers registered in the banking system"
    )
    public List<Customer> listCustomers() {
        return customerService.getAllCustomers();
    }

    @McpTool(
        name = "get_customer_by_id",
        description = "Retrieve a customer using their numeric identifier"
    )
    public Customer getCustomerById(
        @McpToolParam(
            description = "The numeric identifier of the customer",
            required = true
        )
        Long customerId
    ) {
        return customerService.getCustomerById(customerId)
            .orElseThrow(() ->
                new IllegalArgumentException(
                    "Customer not found: " + customerId
                )
            );
    }
}