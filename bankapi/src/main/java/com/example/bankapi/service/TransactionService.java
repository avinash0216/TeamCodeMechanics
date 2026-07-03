package com.example.bankapi.service;

import com.example.bankapi.entity.Account;
import com.example.bankapi.entity.Transaction;
import com.example.bankapi.model.DepositRequest;
import com.example.bankapi.model.DepositResponse;
import com.example.bankapi.model.TransactionStatus;
import com.example.bankapi.model.TransferRequest;
import com.example.bankapi.model.TransferResponse;
import com.example.bankapi.model.WithdrawalRequest;
import com.example.bankapi.model.WithdrawalResponse;
import com.example.bankapi.repository.AccountRepository;
import com.example.bankapi.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service layer for transfer operations.
 * Handles internal transfers between accounts of the same customer.
 * Extracts customer information directly from OAuth2 token.
 * Ensures atomicity: either both transactions are created and accounts updated,
 * or the entire operation rolls back.
 */
@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final AuthenticatedUserService authenticatedUserService;

    public TransactionService(TransactionRepository transactionRepository,
                              AccountRepository accountRepository,
                              AuthenticatedUserService authenticatedUserService) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.authenticatedUserService = authenticatedUserService;
    }

    /**
     * Perform an internal transfer between two accounts of the same customer.
     * Extracts customer_number (sub claim) directly from OAuth2 token,
     * resolves to customerId, validates ownership, and creates transactions atomically.
     *
     * @param request the transfer request with account numbers, amount, and description
     * @return a TransferResult containing the debit and credit transactions
     * @throws IllegalArgumentException if customer not found, accounts don't exist, don't belong to customer,
     *         or amount is invalid
     * @throws RuntimeException if transfer fails or no authentication found
     */
    @Transactional
    public TransferResponse transferBetweenAccountsSameCustomer(TransferRequest request) {
        boolean isTeller = isCurrentUserTeller();

        Long customerId = isTeller ? null : authenticatedUserService.getCurrentCustomerId();

        // Validate amount
        if (request.amount() == null || request.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transfer amount must be positive");
        }

        if (request.fromAccountNumber().equals(request.toAccountNumber())) {
            throw new IllegalArgumentException("Cannot transfer to the same account");
        }

        // Fetch and validate source account by account number
        Account fromAccount = accountRepository.findByAccountNumber(request.fromAccountNumber())
                .orElseThrow(() -> new IllegalArgumentException("Source account not found"));
        if (!isTeller && !fromAccount.getCustomerId().equals(customerId)) {
            throw new IllegalArgumentException("Source account does not belong to this customer");
        }

        // Fetch and validate destination account by account number
        Account toAccount = accountRepository.findByAccountNumber(request.toAccountNumber())
                .orElseThrow(() -> new IllegalArgumentException("Destination account not found"));

        if (!isTeller && !toAccount.getCustomerId().equals(customerId)) {
            throw new IllegalArgumentException("Destination account does not belong to this customer");
        }
        if (isTeller && !toAccount.getCustomerId().equals(fromAccount.getCustomerId())) {
            throw new IllegalArgumentException("Teller transfers must be between accounts owned by the same customer");
        }

        // Validate source account has sufficient balance
        if (fromAccount.getBalance().compareTo(request.amount()) < 0) {
            throw new IllegalArgumentException("Insufficient balance in source account");
        }

        // Create debit transaction (withdrawal from source)
        Transaction debitTransaction = new Transaction();
        debitTransaction.setTxnType("TRANSFER_OUT");
        debitTransaction.setAccountId(fromAccount.getAccountId());
        debitTransaction.setAmount(request.amount());
        debitTransaction.setStatus("COMPLETED");
        debitTransaction.setTxnDate(LocalDateTime.now());
        debitTransaction.setDescription(request.description() != null ? request.description() : "Internal transfer");
        Transaction savedDebitTxn = transactionRepository.save(debitTransaction);

        // Create credit transaction (deposit to destination)
        Transaction creditTransaction = new Transaction();
        creditTransaction.setTxnType("TRANSFER_IN");
        creditTransaction.setAccountId(toAccount.getAccountId());
        creditTransaction.setAmount(request.amount());
        creditTransaction.setStatus("COMPLETED");
        creditTransaction.setTxnDate(LocalDateTime.now());
        creditTransaction.setDescription(request.description() != null ? request.description() : "Internal transfer");
        Transaction savedCreditTxn = transactionRepository.save(creditTransaction);

        // Update account balances
        fromAccount.setBalance(fromAccount.getBalance().subtract(request.amount()));
        toAccount.setBalance(toAccount.getBalance().add(request.amount()));
        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        //Update Transfer table
        //TODO

        return new TransferResponse(savedDebitTxn.getTxnId(), savedCreditTxn.getTxnId(), TransactionStatus.COMPLETE);
    }

    @Transactional
    public DepositResponse depositToAccount(DepositRequest request) {
        validatePositiveAmount(request.amount(), "Deposit amount must be positive");

        String accountNumber = parseAccountNumber(request.accountNumber());
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));

        validateOwnership(account, isCurrentUserTeller());

        account.setBalance(account.getBalance().add(request.amount()));
        accountRepository.save(account);

        Transaction savedDeposit = createAndSaveTransaction(account.getAccountId(), "DEPOSIT", request.amount(), "Deposit");
        return new DepositResponse(savedDeposit.getTxnId(), request.amount(), TransactionStatus.COMPLETE.name());
    }

    @Transactional
    public WithdrawalResponse withdrawFromAccount(WithdrawalRequest request) {
        validatePositiveAmount(request.amount(), "Withdrawal amount must be positive");
        String accountNumber = parseAccountNumber(request.accountNumber());
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));

        validateOwnership(account, isCurrentUserTeller());

        if (account.getBalance().compareTo(request.amount()) < 0) {
            throw new IllegalArgumentException("Insufficient balance in account");
        }

        account.setBalance(account.getBalance().subtract(request.amount()));
        accountRepository.save(account);

        Transaction savedWithdrawal = createAndSaveTransaction(account.getAccountId(), "WITHDRAWAL", request.amount(), "Withdrawal");
        return new WithdrawalResponse(savedWithdrawal.getTxnId(), request.amount(), TransactionStatus.COMPLETE.name());
    }

    /**
     * Get a transaction by its ID.
     *
     * @param txnId the transaction ID
     * @return the Transaction wrapped in Optional
     */
    public Optional<Transaction> getTransactionById(String txnId) {
        return transactionRepository.findById(txnId);
    }

    private boolean isCurrentUserTeller() {
        List<String> roles = authenticatedUserService.getCurrentJwt().getClaimAsStringList("roles");
        if (roles == null) {
            roles = List.of();
        }
        return roles.stream().anyMatch("teller"::equalsIgnoreCase);
    }

    private void validateOwnership(Account account, boolean isTeller) {
        if (isTeller) {
            return;
        }
        Long customerId = authenticatedUserService.getCurrentCustomerId();
        if (!account.getCustomerId().equals(customerId)) {
            throw new IllegalArgumentException("Account does not belong to this customer");
        }
    }

    private void validatePositiveAmount(BigDecimal amount, String message) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(message);
        }
    }

    private String parseAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.isBlank()) {
            throw new IllegalArgumentException("Account Number is required");
        }
        return accountNumber;
    }

    private Transaction createAndSaveTransaction(Long accountId, String transactionType, BigDecimal amount, String description) {
        Transaction transaction = new Transaction();
        transaction.setTxnType(transactionType);
        transaction.setAccountId(accountId);
        transaction.setAmount(amount);
        transaction.setStatus("COMPLETED");
        transaction.setTxnDate(LocalDateTime.now());
        transaction.setDescription(description);
        return transactionRepository.save(transaction);
    }
}
