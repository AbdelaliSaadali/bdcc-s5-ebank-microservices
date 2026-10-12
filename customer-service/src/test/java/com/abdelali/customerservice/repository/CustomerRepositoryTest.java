package com.abdelali.customerservice.repository;

import com.abdelali.customerservice.entities.Customer;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    void shouldSaveAndFindCustomer() {

        // 1. Create a Java object
        Customer customer = Customer.builder()
                .name("Sara")
                .email("sara@example.com")
                .build();

        // 2. Save to the database
        Customer saved = customerRepository.save(customer);

        // 3. Verify generated ID
        assertThat(saved.getId()).isNotNull();

        // 4. Retrieve from database
        Customer found = customerRepository
                .findById(saved.getId())
                .orElseThrow();

        // 5. Verify data
        assertThat(found.getName()).isEqualTo("Sara");
        assertThat(found.getEmail()).isEqualTo("sara@example.com");
    }
}