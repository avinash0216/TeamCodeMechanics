package com.example.bankapi.model;

import java.math.BigDecimal;
import java.time.Instant;

public record TransactionSummary(
        String transactionId,
        String accountNumber,
        BigDecimal amount,
        Instant timestamp,
        String type,
        String status
) implements Transaction {}