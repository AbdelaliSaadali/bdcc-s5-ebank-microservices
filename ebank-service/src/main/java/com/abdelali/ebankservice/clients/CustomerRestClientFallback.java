package com.abdelali.ebankservice.clients;

import com.abdelali.ebankservice.model.Customer;
import org.springframework.stereotype.Component;

@Component
public class CustomerRestClientFallback implements CustomerRestClient {

    @Override
    public Customer getCustomerById(Long id) {
        return null;
    }
}