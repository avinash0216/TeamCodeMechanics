package com.example.bankapi.service;

import com.example.bankapi.entity.Account;
import com.example.bankapi.repository.AccountRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service layer for account operations.
 * Handles business logic for account queries, validation, and operations.
 */
@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final AuthenticatedUserService authenticatedUserService;

    public AccountService(AccountRepository accountRepository,
                          AuthenticatedUserService authenticatedUserService) {
        this.accountRepository = accountRepository;
        this.authenticatedUserService = authenticatedUserService;
    }

    public List<com.example.bankapi.model.Account> getAllAccounts() {
        Long customerId = authenticatedUserService.getCurrentCustomerId();
        List<Account> entityAccounts = accountRepository.findByCustomerId(customerId);
        return convertAccountEntitiesToModels(entityAccounts);
    }

    public List<com.example.bankapi.model.Account> getAllAccountsUnscoped() {
        List<Account> entityAccounts = accountRepository.findAll();
        return convertAccountEntitiesToModels(entityAccounts);
    }

    /**
     * Get all accounts for a given customer.
     * Used to retrieve the list of accounts a customer owns.
     *
     * @param customerId the customer's ID
     * @return list of accounts for the customer, empty list if none
     */
    public List<com.example.bankapi.model.Account> getAccountsByCustomer(Long customerId) {
        List<Account> entityAccounts = accountRepository.findByCustomerId(customerId);
        return convertAccountEntitiesToModels(entityAccounts);
    }

    /**
     * Get a specific account by its account number.
     *
     * @param accountNumber the unique account number
     * @return the Account wrapped in Optional
     */
    public Optional<Account> getAccountByNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber);
    }

    /**
     * Verify that a customer owns a specific account.
     * Used before processing transactions to ensure authorization.
     *
     * @param customerId the customer's ID
     * @param accountNumber the account number to verify
     * @return true if the customer owns the account, false otherwise
     */
    public boolean customerOwnsAccount(Long customerId, String accountNumber) {
        return accountRepository.findByCustomerIdAndAccountNumber(customerId, accountNumber)
                .isPresent();
    }

    /**
     * Get a specific account by ID.
     *
     * @param accountId the account ID
     * @return the Account wrapped in Optional
     */
    public Optional<com.example.bankapi.model.Account> getAccountById(Long accountId) {
        Optional<Account> account = accountRepository.findById(accountId);
        return account.map(this::convertAccountEntityToModel);
    }

    /**
     * Create a new account.
     *
     * @param account the account to create
     * @return the created account with generated ID
     */
    public Account createAccount(Account account) {
        return accountRepository.save(account);
    }

    /**
     * Update an existing account (e.g., balance, status).
     *
     * @param account the account with updated values
     * @return the updated account
     */
    public Account updateAccount(Account account) {
        return accountRepository.save(account);
    }

    /**
     * Convert entity accounts to model accounts.
     *
     * @param entityAccounts the list of entity accounts
     * @return list of model accounts
     */
    private List<com.example.bankapi.model.Account> convertAccountEntitiesToModels(List<Account> entityAccounts) {
        return entityAccounts.stream()
                .map(this::convertAccountEntityToModel)
                .collect(Collectors.toList());
    }

    /**
     * Convert a single entity account to model account.
     *
     * @param entityAccount the entity account
     * @return the model account
     */
    private com.example.bankapi.model.Account convertAccountEntityToModel(Account entityAccount) {
        // Map entity fields to model using the entity's accessible properties
        return new com.example.bankapi.model.Account(
                entityAccount.getAccountNumber(),
                String.valueOf(entityAccount.getCustomerId()),
                entityAccount.getAccountType(),
                entityAccount.getBalance()
        );
    }

}
