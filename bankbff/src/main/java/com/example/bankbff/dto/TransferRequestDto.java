package com.example.bankbff.dto;

import java.math.BigDecimal;

public record TransferRequestDto(
        String fromAccountNumber,
        String toAccountNumber,
        BigDecimal amount
) {}