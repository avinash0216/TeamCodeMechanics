package com.example.bankapi.service;

import com.example.bankapi.entity.Account;
import com.example.bankapi.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AccountServiceTest {

    private AccountService accountService;
    private AccountRepository accountRepository;
    private AuthenticatedUserService authenticatedUserService;

    @BeforeEach
    void setUp() {
        accountRepository = mock(AccountRepository.class);
        authenticatedUserService = mock(AuthenticatedUserService.class);
        accountService = new AccountService(accountRepository, authenticatedUserService);
    }

    // ===== GET ACCOUNT TESTS =====

    @Test
    void getAccountByNumber_ReturnsAccount() {
        Account account = createAccount(1L, "ACC-001", new BigDecimal("500.00"), "ACTIVE", 1L);
        when(accountRepository.findByAccountNumber("ACC-001")).thenReturn(Optional.of(account));

        Optional<Account> result = accountService.getAccountByNumber("ACC-001");

        assertThat(result).isPresent();
        assertThat(result.get().getAccountNumber()).isEqualTo("ACC-001");
        assertThat(result.get().getBalance()).isEqualTo(new BigDecimal("500.00"));

        verify(accountRepository).findByAccountNumber("ACC-001");
    }

    @Test
    void getAccountByNumber_ReturnsEmpty_WhenAccountNotFound() {
        when(accountRepository.findByAccountNumber("ACC-NOTFOUND")).thenReturn(Optional.empty());

        Optional<Account> result = accountService.getAccountByNumber("ACC-NOTFOUND");

        assertThat(result).isEmpty();
        verify(accountRepository).findByAccountNumber("ACC-NOTFOUND");
    }

    @Test
    void getAccountById_ReturnsAccountModel() {
        Account account = createAccount(1L, "ACC-002", new BigDecimal("1000.00"), "ACTIVE", 1L);
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        Optional<com.example.bankapi.model.Account> result = accountService.getAccountById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().accountNumber()).isEqualTo("ACC-002");
        assertThat(result.get().balance()).isEqualTo(new BigDecimal("1000.00"));
    }

    @Test
    void getAccountById_ReturnsEmpty_WhenAccountNotFound() {
        when(accountRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<com.example.bankapi.model.Account> result = accountService.getAccountById(999L);

        assertThat(result).isEmpty();
    }

    // ===== GET ACCOUNTS BY CUSTOMER TESTS =====

    @Test
    void getAccountsByCustomer_ReturnsMultipleAccounts() {
        Long customerId = 1L;
        List<Account> accounts = Arrays.asList(
                createAccount(1L, "ACC-001", new BigDecimal("500.00"), "ACTIVE", customerId),
                createAccount(2L, "ACC-002", new BigDecimal("1000.00"), "ACTIVE", customerId)
        );

        when(accountRepository.findByCustomerId(customerId)).thenReturn(accounts);

        List<com.example.bankapi.model.Account> result = accountService.getAccountsByCustomer(customerId);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).accountNumber()).isEqualTo("ACC-001");
        assertThat(result.get(1).accountNumber()).isEqualTo("ACC-002");

        verify(accountRepository).findByCustomerId(customerId);
    }

    @Test
    void getAccountsByCustomer_ReturnsEmptyList_WhenNoAccountsFound() {
        when(accountRepository.findByCustomerId(999L)).thenReturn(Arrays.asList());

        List<com.example.bankapi.model.Account> result = accountService.getAccountsByCustomer(999L);

        assertThat(result).isEmpty();
    }

    @Test
    void getAllAccounts_ReturnsCurrentCustomerAccounts() {
        Long customerId = 1L;
        List<Account> accounts = Arrays.asList(
                createAccount(1L, "ACC-001", new BigDecimal("500.00"), "ACTIVE", customerId),
                createAccount(2L, "ACC-002", new BigDecimal("1000.00"), "ACTIVE", customerId)
        );

        when(authenticatedUserService.getCurrentCustomerId()).thenReturn(customerId);
        when(accountRepository.findByCustomerId(customerId)).thenReturn(accounts);

        List<com.example.bankapi.model.Account> result = accountService.getAllAccounts();

        assertThat(result).hasSize(2);
        verify(authenticatedUserService).getCurrentCustomerId();
        verify(accountRepository).findByCustomerId(customerId);
    }

    @Test
    void getAllAccountsUnscoped_ReturnsAllAccounts() {
        List<Account> accounts = Arrays.asList(
                createAccount(1L, "ACC-001", new BigDecimal("500.00"), "ACTIVE", 1L),
                createAccount(2L, "ACC-002", new BigDecimal("1000.00"), "ACTIVE", 2L)
        );

        when(accountRepository.findAll()).thenReturn(accounts);

        List<com.example.bankapi.model.Account> result = accountService.getAllAccountsUnscoped();

        assertThat(result).hasSize(2);
        verify(accountRepository).findAll();
    }

    // ===== CUSTOMER OWNERSHIP TESTS =====

    @Test
    void customerOwnsAccount_ReturnsTrue_WhenCustomerOwnsAccount() {
        Account account = createAccount(1L, "ACC-001", new BigDecimal("500.00"), "ACTIVE", 1L);
        when(accountRepository.findByCustomerIdAndAccountNumber(1L, "ACC-001"))
                .thenReturn(Optional.of(account));

        boolean result = accountService.customerOwnsAccount(1L, "ACC-001");

        assertThat(result).isTrue();
        verify(accountRepository).findByCustomerIdAndAccountNumber(1L, "ACC-001");
    }

    @Test
    void customerOwnsAccount_ReturnsFalse_WhenCustomerDoesNotOwnAccount() {
        when(accountRepository.findByCustomerIdAndAccountNumber(1L, "ACC-999"))
                .thenReturn(Optional.empty());

        boolean result = accountService.customerOwnsAccount(1L, "ACC-999");

        assertThat(result).isFalse();
    }

    @Test
    void customerOwnsAccount_ReturnsFalse_WhenAccountBelongsToAnotherCustomer() {
        Account account = createAccount(1L, "ACC-001", new BigDecimal("500.00"), "ACTIVE", 2L);
        when(accountRepository.findByCustomerIdAndAccountNumber(1L, "ACC-001"))
                .thenReturn(Optional.empty());

        boolean result = accountService.customerOwnsAccount(1L, "ACC-001");

        assertThat(result).isFalse();
    }

    // ===== CREATE AND UPDATE TESTS =====

    @Test
    void createAccount_SavesAndReturnsAccount() {
        Account newAccount = createAccount(null, "ACC-NEW", new BigDecimal("100.00"), "ACTIVE", 1L);
        Account savedAccount = createAccount(5L, "ACC-NEW", new BigDecimal("100.00"), "ACTIVE", 1L);

        when(accountRepository.save(any(Account.class))).thenReturn(savedAccount);

        Account result = accountService.createAccount(newAccount);

        assertThat(result).isNotNull();
        assertThat(result.getAccountId()).isEqualTo(5L);
        assertThat(result.getAccountNumber()).isEqualTo("ACC-NEW");

        verify(accountRepository).save(any(Account.class));
    }

    @Test
    void updateAccount_UpdatesBalance() {
        Account accountToUpdate = createAccount(1L, "ACC-001", new BigDecimal("600.00"), "ACTIVE", 1L);
        when(accountRepository.save(any(Account.class))).thenReturn(accountToUpdate);

        Account result = accountService.updateAccount(accountToUpdate);

        assertThat(result.getBalance()).isEqualTo(new BigDecimal("600.00"));
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    void updateAccount_UpdatesStatus() {
        Account accountToUpdate = createAccount(1L, "ACC-001", new BigDecimal("500.00"), "CLOSED", 1L);
        when(accountRepository.save(any(Account.class))).thenReturn(accountToUpdate);

        Account result = accountService.updateAccount(accountToUpdate);

        assertThat(result.getAccountStatus()).isEqualTo("CLOSED");
        verify(accountRepository).save(any(Account.class));
    }

    // ===== HELPER METHODS =====

    private Account createAccount(Long accountId, String accountNumber, BigDecimal balance, String status, Long customerId) {
        Account account = new Account();
        account.setAccountId(accountId);
        account.setAccountNumber(accountNumber);
        account.setBalance(balance);
        account.setAccountStatus(status);
        account.setCustomerId(customerId);
        account.setAccountType("CHECKING");
        return account;
    }
}
