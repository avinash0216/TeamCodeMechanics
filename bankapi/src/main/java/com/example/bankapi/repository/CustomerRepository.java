package com.example.bankapi.repository;

import com.example.bankapi.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for Customer entity lookups.
 * The primary lookup is by customer_number, which is the canonical business identifier
 * that appears in the OAuth2 token's sub claim.
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    /**
     * Find a customer by their customer_number.
     * This is the key lookup for resolving token ownership.
     *
     * @param customerNumber the unique customer business identifier (e.g., "487-978493")
     * @return the Customer if found
     */
    Optional<Customer> findByCustomerNumber(String customerNumber);

}
