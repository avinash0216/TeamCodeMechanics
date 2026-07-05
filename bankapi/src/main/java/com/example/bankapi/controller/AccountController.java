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

    // TODO 1: Add a POST endpoint that accepts an Account in the request body
    // and returns 201 Created with the account in the response body.
    // The endpoint does not need to persist the account -- this is a stub.
    // Annotate the parameter with @RequestBody.
    // Use ResponseEntity.status(HttpStatus.CREATED).body(account) as the return value.

    @PostMapping
    public ResponseEntity<Account> accounts(Account account){
        return ResponseEntity.status(HttpStatus.CREATED).body(account);
    }

    // TODO 6: Complete this endpoint.
// @AuthenticationPrincipal instructs Spring Security to inject the validated Jwt
// from the SecurityContext directly as a method parameter.
// This is cleaner than calling SecurityContextHolder.getContext() manually.
    @GetMapping("/me")
    public Map<String, Object> getCurrentUser(@AuthenticationPrincipal Jwt jwt) {
        return authenticatedUserService.extractUserDetails(jwt);
    }

    // TODO 24: Add this endpoint to AccountController.
// It is protected and requires an authenticated caller.
// The inbound request uses the caller's token.
// The outbound call to the downstream service uses the service's own token.
    @GetMapping("/downstream")
    public List<Account> getFromDownstream() {
        // TODO: call downstreamAccountService.fetchAllFromDownstream() and return the result
        return downstreamAccountService.fetchAllFromDownstream();
    }

}