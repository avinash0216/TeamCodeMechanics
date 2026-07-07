package com.example.bankapi.service;

import com.example.bankapi.entity.Account;
import com.example.bankapi.entity.Transaction;
import com.example.bankapi.exception.AccountNotFoundException;
import com.example.bankapi.exception.BusinessRuleException;
import com.example.bankapi.exception.MalformedRequestException;
import com.example.bankapi.model.DepositRequest;
import com.example.bankapi.model.DepositResponse;
import com.example.bankapi.model.WithdrawalRequest;
import com.example.bankapi.model.WithdrawalResponse;
import com.example.bankapi.repository.AccountRepository;
import com.example.bankapi.repository.TransactionRepository;
import com.example.bankapi.repository.TransferRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.oauth2.jwt.Jwt;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;

class TransactionServiceTest {

    private TransactionService transactionService;
    private TransactionRepository transactionRepository;
    private AccountRepository accountRepository;
    private TransferRepository transferRepository;
    private AuthenticatedUserService authenticatedUserService;
    private ApplicationEventPublisher eventPublisher;

    @BeforeEach
    void setUp() {
        transactionRepository = mock(TransactionRepository.class);
        accountRepository = mock(AccountRepository.class);
        transferRepository = mock(TransferRepository.class);
        authenticatedUserService = mock(AuthenticatedUserService.class);
        eventPublisher = mock(ApplicationEventPublisher.class);

        // Mock JWT to prevent null pointer when checking roles
        org.springframework.security.oauth2.jwt.Jwt mockJwt = mock(org.springframework.security.oauth2.jwt.Jwt.class);
        when(mockJwt.getClaimAsStringList("roles")).thenReturn(java.util.List.of("account_holder"));
        when(authenticatedUserService.getCurrentJwt()).thenReturn(mockJwt);

        transactionService = new TransactionService(
                transactionRepository,
                accountRepository,
                transferRepository,
                authenticatedUserService,
                eventPublisher
        );
    }

    // ===== DEPOSIT TESTS =====

    @Test
    void depositToAccount_Success() {
        Account account = createAccount(1L, "ACC-001", new BigDecimal("500.00"), "ACTIVE", 1L);
        Transaction savedTransaction = createTransaction("TXN-001", 1L, new BigDecimal("100.00"), "DEPOSIT");

        when(accountRepository.findByAccountNumber("ACC-001")).thenReturn(Optional.of(account));
        when(authenticatedUserService.getCurrentCustomerId()).thenReturn(1L);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(savedTransaction);

        DepositRequest request = new DepositRequest("ACC-001", new BigDecimal("100.00"));
        DepositResponse response = transactionService.depositToAccount(request);

        assertThat(response).isNotNull();
        assertThat(response.accountNumber()).isEqualTo("ACC-001");
        assertThat(response.amount()).isEqualTo(new BigDecimal("100.00"));
        assertThat(response.transactionId()).isEqualTo("TXN-001");
        assertThat(response.status()).isEqualTo("COMPLETED");

        verify(accountRepository).findByAccountNumber("ACC-001");
        verify(accountRepository).save(any(Account.class));
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void depositToAccount_ThrowsMalformedRequestException_NonPositiveAmount() {
        assertThatThrownBy(() -> transactionService.depositToAccount(
                new DepositRequest("ACC-001", new BigDecimal("-50.00"))
        ))
        .isInstanceOf(MalformedRequestException.class)
        .hasMessageContaining("Amount must be positive");
    }

    @Test
    void depositToAccount_ThrowsMalformedRequestException_ZeroAmount() {
        assertThatThrownBy(() -> transactionService.depositToAccount(
                new DepositRequest("ACC-001", new BigDecimal("0.00"))
        ))
        .isInstanceOf(MalformedRequestException.class)
        .hasMessageContaining("Amount must be positive");
    }

    @Test
    void depositToAccount_ThrowsMalformedRequestException_MissingAccountNumber() {
        assertThatThrownBy(() -> transactionService.depositToAccount(
                new DepositRequest(null, new BigDecimal("100.00"))
        ))
        .isInstanceOf(MalformedRequestException.class)
        .hasMessageContaining("Account number is required");
    }

    @Test
    void depositToAccount_ThrowsAccountNotFoundException() {
        when(accountRepository.findByAccountNumber("ACC-NOTFOUND")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transactionService.depositToAccount(
                new DepositRequest("ACC-NOTFOUND", new BigDecimal("100.00"))
        ))
        .isInstanceOf(AccountNotFoundException.class);

        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    void depositToAccount_ThrowsBusinessRuleException_InactiveAccount() {
        Account inactiveAccount = createAccount(1L, "ACC-001", new BigDecimal("500.00"), "INACTIVE", 1L);
        when(accountRepository.findByAccountNumber("ACC-001")).thenReturn(Optional.of(inactiveAccount));
        when(authenticatedUserService.getCurrentCustomerId()).thenReturn(1L);

        assertThatThrownBy(() -> transactionService.depositToAccount(
                new DepositRequest("ACC-001", new BigDecimal("100.00"))
        ))
        .isInstanceOf(BusinessRuleException.class)
        .hasMessageContaining("Account is not active");

        verify(accountRepository, never()).save(any(Account.class));
    }

    // ===== WITHDRAWAL TESTS =====

    @Test
    void withdrawFromAccount_Success() {
        Account account = createAccount(1L, "ACC-002", new BigDecimal("500.00"), "ACTIVE", 1L);
        Transaction savedTransaction = createTransaction("TXN-002", 1L, new BigDecimal("150.00"), "WITHDRAWAL");

        when(accountRepository.findByAccountNumber("ACC-002")).thenReturn(Optional.of(account));
        when(authenticatedUserService.getCurrentCustomerId()).thenReturn(1L);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(savedTransaction);

        WithdrawalRequest request = new WithdrawalRequest("ACC-002", new BigDecimal("150.00"));
        WithdrawalResponse response = transactionService.withdrawFromAccount(request);

        assertThat(response).isNotNull();
        assertThat(response.accountNumber()).isEqualTo("ACC-002");
        assertThat(response.amount()).isEqualTo(new BigDecimal("150.00"));
        assertThat(response.transactionId()).isEqualTo("TXN-002");
        assertThat(response.status()).isEqualTo("COMPLETED");

        verify(accountRepository).findByAccountNumber("ACC-002");
        verify(accountRepository).save(any(Account.class));
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void withdrawFromAccount_ThrowsMalformedRequestException_NonPositiveAmount() {
        assertThatThrownBy(() -> transactionService.withdrawFromAccount(
                new WithdrawalRequest("ACC-002", new BigDecimal("0.00"))
        ))
        .isInstanceOf(MalformedRequestException.class)
        .hasMessageContaining("Amount must be positive");
    }

    @Test
    void withdrawFromAccount_ThrowsAccountNotFoundException() {
        when(accountRepository.findByAccountNumber("ACC-NOTFOUND")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transactionService.withdrawFromAccount(
                new WithdrawalRequest("ACC-NOTFOUND", new BigDecimal("100.00"))
        ))
        .isInstanceOf(AccountNotFoundException.class);

        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void withdrawFromAccount_ThrowsBusinessRuleException_InsufficientFunds() {
        Account account = createAccount(1L, "ACC-003", new BigDecimal("50.00"), "ACTIVE", 1L);
        when(accountRepository.findByAccountNumber("ACC-003")).thenReturn(Optional.of(account));
        when(authenticatedUserService.getCurrentCustomerId()).thenReturn(1L);

        assertThatThrownBy(() -> transactionService.withdrawFromAccount(
                new WithdrawalRequest("ACC-003", new BigDecimal("100.00"))
        ))
        .isInstanceOf(BusinessRuleException.class)
        .hasMessageContaining("Insufficient balance");

        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    void withdrawFromAccount_ThrowsBusinessRuleException_InactiveAccount() {
        Account inactiveAccount = createAccount(1L, "ACC-004", new BigDecimal("500.00"), "INACTIVE", 1L);
        when(accountRepository.findByAccountNumber("ACC-004")).thenReturn(Optional.of(inactiveAccount));
        when(authenticatedUserService.getCurrentCustomerId()).thenReturn(1L);

        assertThatThrownBy(() -> transactionService.withdrawFromAccount(
                new WithdrawalRequest("ACC-004", new BigDecimal("100.00"))
        ))
        .isInstanceOf(BusinessRuleException.class)
        .hasMessageContaining("Account is not active");

        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    void withdrawFromAccount_DebitsAccountBalance() {
        Account account = createAccount(1L, "ACC-005", new BigDecimal("500.00"), "ACTIVE", 1L);
        Transaction savedTransaction = createTransaction("TXN-003", 1L, new BigDecimal("200.00"), "WITHDRAWAL");

        when(accountRepository.findByAccountNumber("ACC-005")).thenReturn(Optional.of(account));
        when(authenticatedUserService.getCurrentCustomerId()).thenReturn(1L);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(savedTransaction);

        WithdrawalRequest request = new WithdrawalRequest("ACC-005", new BigDecimal("200.00"));
        transactionService.withdrawFromAccount(request);

        verify(accountRepository).save(any(Account.class));
        assertThat(account.getBalance()).isEqualTo(new BigDecimal("300.00"));
    }

    // ===== HELPER METHODS =====

    private Account createAccount(Long accountId, String accountNumber, BigDecimal balance, String status, Long customerId) {
        Account account = new Account();
        account.setAccountId(accountId);
        account.setAccountNumber(accountNumber);
        account.setBalance(balance);
        account.setAccountStatus(status);
        account.setCustomerId(customerId);
        return account;
    }

    private Transaction createTransaction(String txnId, Long accountId, BigDecimal amount, String type) {
        Transaction transaction = new Transaction();
        transaction.setTxnId(txnId);
        transaction.setAccountId(accountId);
        transaction.setAmount(amount);
        transaction.setTxnType(type);
        transaction.setStatus("COMPLETED");
        transaction.setTxnDate(LocalDateTime.now());
        return transaction;
    }
}
