package com.example.bankbff.dto;

import java.math.BigDecimal;

public record PaymentRequestDto(String accountId, BigDecimal amount, String payee) {
}
