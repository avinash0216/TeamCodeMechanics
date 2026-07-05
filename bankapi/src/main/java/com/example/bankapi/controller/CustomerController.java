package com.example.bankapi.controller;


import com.example.bankapi.model.Account;
import com.example.bankapi.service.AccountService;
import com.example.bankapi.service.AuthenticatedUserService;
import com.example.bankapi.service.CustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {

    private final AuthenticatedUserService authenticatedUserService;
    private final AccountService accountService;
    private final CustomerService customerService;

    public CustomerController(AuthenticatedUserService authenticatedUserService, AccountService accountService, CustomerService customerService) {
        this.authenticatedUserService = authenticatedUserService;
        this.accountService = accountService;
        this.customerService = customerService;
    }

    @GetMapping("/{customerNumber}/accounts")
    public List<Account> getAccountsByCustomer(@PathVariable String customerNumber) {
        // resolve the customer id and convert from Optional<Long> to Long
        Long customerId = customerService.resolveCustomerId(customerNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));
        if (!authenticatedUserService.hasRole("teller")) {
            authenticatedUserService.getCurrentCustomerId();
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Customers can only access their own accounts");
        }
        return accountService.getAccountsByCustomer(customerId);
    }
}
