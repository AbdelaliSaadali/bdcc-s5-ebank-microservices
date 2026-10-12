package com.abdelali.customerservice.web;

import com.abdelali.customerservice.entities.Customer;
import com.abdelali.customerservice.service.CustomerService;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/customers")
public class CustomerRestController {

    private final CustomerService customerService;

    public CustomerRestController(CustomerService customerService) {
        this.customerService = customerService;
    }

    // GET /customers
    @GetMapping
    public List<Customer> getAllCustomers() {
        return customerService.getAllCustomers();
    }

    // GET /customers/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Customer> getCustomerById(
            @PathVariable("id") Long id) {

        return customerService.getCustomerById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // POST /customers
    @PostMapping
    public ResponseEntity<Customer> createCustomer(
            @RequestBody Customer customer) {

        Customer saved = customerService.createCustomer(customer);

        URI location = URI.create("/customers/" + saved.getId());

        return ResponseEntity.created(location).body(saved);
    }

    // Handle invalid customer data
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleInvalidCustomer(
            IllegalArgumentException exception) {

        return ResponseEntity.badRequest()
                .body(exception.getMessage());
    }
}