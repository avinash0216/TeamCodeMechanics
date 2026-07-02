package com.example.bankapi.service;

import com.example.bankapi.entity.Customer;
import com.example.bankapi.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service layer for customer operations.
 * Handles customer lookups and resolution by customer_number.
 */
@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    /**
     * Resolve a customer_number (from OAuth2 token sub claim) to a customerId.
     * Used to extract the database ID from the token's business identifier.
     *
     * @param customerNumber the customer number (e.g., "487-978493" from token sub)
     * @return customerId if found
     * @throws IllegalArgumentException if customer not found
     */
    public Long resolveCustomerId(String customerNumber) {
        return customerRepository.findByCustomerNumber(customerNumber)
                .map(Customer::getCustomerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + customerNumber));
    }

    /**
     * Get a customer by their customer_number.
     *
     * @param customerNumber the unique customer business identifier
     * @return the Customer wrapped in Optional
     */
    public Optional<Customer> getByCustomerNumber(String customerNumber) {
        return customerRepository.findByCustomerNumber(customerNumber);
    }

    /**
     * Get a customer by their ID.
     *
     * @param customerId the customer ID
     * @return the Customer wrapped in Optional
     */
    public Optional<Customer> getById(Long customerId) {
        return customerRepository.findById(customerId);
    }
}
