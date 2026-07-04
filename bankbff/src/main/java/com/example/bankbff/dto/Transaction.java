package com.example.bankbff.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;
import java.time.Instant;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Transaction(
        String transactionId,
        String accountNumber,
        BigDecimal amount,
        Instant timestamp,
        String status
) {}
