package com.example.bankapi.controller;

import com.example.bankapi.model.DepositRequest;
import com.example.bankapi.model.DepositResponse;
import com.example.bankapi.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/deposits")
public class DepositController {

    private final TransactionService transactionService;

    public DepositController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/deposits")
    public DepositResponse deposit(@Valid @RequestBody DepositRequest request) {
        return transactionService.depositToAccount(request);
    }
}
