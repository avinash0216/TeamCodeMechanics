package com.example.bankapi.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record TransferRequest(
        @NotNull(message = "From account ID is required")
        Long fromAccountId,
        
        @NotNull(message = "To account ID is required")
        Long toAccountId,
        
        @Positive(message = "Transfer amount must be positive")
        BigDecimal amount,
        
        String description
) {}