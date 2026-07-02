package com.example.bank.bff.dto;

import java.math.BigDecimal;

public record TransferRequestDto(
        String fromAccountId,
        String toAccountId,
        BigDecimal amount
) {}