package com.example.bankbff.dto;

import java.math.BigDecimal;

public record TransferRequest(
        String fromAccountNumber,
        String toAccountNumber,
        BigDecimal amount
) {}