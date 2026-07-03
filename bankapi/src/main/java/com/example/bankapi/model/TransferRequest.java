package com.example.bankapi.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record TransferRequest(
        @NotBlank(message = "From account number is required")
        String fromAccountNumber,
        
        @NotBlank(message = "To account number is required")
        String toAccountNumber,
        
        @Positive(message = "Transfer amount must be positive")
        BigDecimal amount,
        
        String description
) {}