package com.abdelali.customerservice.service;

import com.abdelali.customerservice.entities.Customer;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class CustomerServiceTest {

    @Autowired
    private CustomerService customerService;

    @Test
    void shouldCreateAndRetrieveCustomer() {

        Customer customer = Customer.builder()
                .name("Sara")
                .email("sara@example.com")
                .build();

        Customer saved = customerService.createCustomer(customer);

        assertThat(saved.getId()).isNotNull();

        Customer found = customerService
                .getCustomerById(saved.getId())
                .orElseThrow();

        assertThat(found.getName()).isEqualTo("Sara");
    }

    @Test
    void shouldRejectCustomerWithoutName() {

        Customer customer = Customer.builder()
                .email("sara@example.com")
                .build();

        assertThatThrownBy(
                () -> customerService.createCustomer(customer)
        )
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Customer name is required");
    }
}