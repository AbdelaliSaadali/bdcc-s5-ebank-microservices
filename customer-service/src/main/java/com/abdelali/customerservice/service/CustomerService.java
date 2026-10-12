package com.abdelali.customerservice.service;

import com.abdelali.customerservice.entities.Customer;
import com.abdelali.customerservice.repository.CustomerRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    // Constructor injection
    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    // Retrieve all customers
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    // Find a customer by ID
    public Optional<Customer> getCustomerById(Long id) {
        return customerRepository.findById(id);
    }

    // Create a new customer
    public Customer createCustomer(Customer customer) {

        if (customer.getName() == null ||
                customer.getName().isBlank()) {
            throw new IllegalArgumentException(
                    "Customer name is required");
        }

        if (customer.getEmail() == null ||
                customer.getEmail().isBlank()) {
            throw new IllegalArgumentException(
                    "Customer email is required");
        }

        return customerRepository.save(customer);
    }
}