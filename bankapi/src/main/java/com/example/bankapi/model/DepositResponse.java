package com.example.bankapi.model;

import java.math.BigDecimal;
import java.time.Instant;

public record DepositResponse(
        String transactionId,
        String accountNumber,
        BigDecimal amount,
        Instant timestamp,
        String status
) implements Transaction {}
