package com.example.bankapi.controller;

import com.example.bankapi.model.WithdrawalRequest;
import com.example.bankapi.model.WithdrawalResponse;
import com.example.bankapi.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/withdrawals")
public class WithdrawalController {

    private final TransactionService transactionService;

    public WithdrawalController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public WithdrawalResponse withdraw(@Valid @RequestBody WithdrawalRequest request) {
        return transactionService.withdrawFromAccount(request);
    }
}
