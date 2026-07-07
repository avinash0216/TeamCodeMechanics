package com.example.bankapi.controller;

import com.example.bankapi.model.Account;
import com.example.bankapi.service.AccountService;
import com.example.bankapi.service.AuthenticatedUserService;
import com.example.bankapi.service.AuditService;
import com.example.bankapi.service.DownstreamAccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {

    private final AccountService accountService;
    private final AuditService auditService;
    private final DownstreamAccountService downstreamAccountService;
    private final AuthenticatedUserService authenticatedUserService;

    public AccountController (AuditService auditService,
                              DownstreamAccountService downstreamAccountService,
                              AccountService accountService,
                              AuthenticatedUserService authenticatedUserService){
        this.auditService = auditService;
        this.downstreamAccountService = downstreamAccountService;
        this.accountService = accountService;
        this.authenticatedUserService = authenticatedUserService;
    }


    @GetMapping("/accounts")
    public List<Account> getMyAccounts() {
        if (authenticatedUserService.hasRole("teller")) {
            return accountService.getAllAccountsUnscoped();
        }
        return accountService.getAllAccounts();
    }

    @GetMapping("/{accountId}")
    @PreAuthorize("hasRole('teller') or @authenticatedUserService.getCurrentCustomerId() == #accountId")
    public ResponseEntity<Account> getByAccountId(@PathVariable String accountId) {
        auditService.logEvent("READ_ACCOUNT",accountId);
        return accountService.getAccountById(Long.valueOf(accountId))
                .map(account -> {
                    if (!authenticatedUserService.hasRole("teller")
                            && !String.valueOf(authenticatedUserService.getCurrentCustomerId()).equals(account.customerId())) {
                        throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                                "Customers can only access their own accounts");
                    }
                    return ResponseEntity.ok(account);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('teller')")
    public ResponseEntity<Account> accounts(Account account){
        return ResponseEntity.status(HttpStatus.CREATED).body(account);
    }
}