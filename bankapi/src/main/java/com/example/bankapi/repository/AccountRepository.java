package com.example.bankapi.repository;

import com.example.bankapi.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Account entity lookups.
 * Provides methods to find accounts by customer, by account number, and to list
 * all accounts for a given customer.
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {


    List<Account> findAll();

    /**
     * Find all accounts for a given customer.
     * Used to list a customer's accounts and verify ownership.
     *
     * @param customerId the customer's ID
     * @return list of accounts belonging to the customer
     */
    List<Account> findByCustomerId(Long customerId);

    /**
     * Find a single account by its account number.
     * Account numbers are unique, so this returns at most one account.
     *
     * @param accountNumber the unique account number (e.g., "128-9878-001")
     * @return the Account if found
     */
    Optional<Account> findByAccountNumber(String accountNumber);

    /**
     * Find an account by customer ID and account number.
     * Used to verify a customer owns a specific account before processing transactions.
     *
     * @param customerId the customer's ID
     * @param accountNumber the account number
     * @return the Account if found and belongs to the customer
     */
    Optional<Account> findByCustomerIdAndAccountNumber(Long customerId, String accountNumber);
}
