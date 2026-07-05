package com.example.bankapi.controller;

import com.example.bankapi.model.DepositRequest;
import com.example.bankapi.model.DepositResponse;
import com.example.bankapi.service.TransactionService;
import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasRole('teller')")
    public ResponseEntity<DepositResponse> deposit(@Valid @RequestBody DepositRequest request) {
        DepositResponse response = transactionService.depositToAccount(request);
        return ResponseEntity.created(URI.create("/api/v1/transactions/" + response.transactionId())).body(response);
    }
}
