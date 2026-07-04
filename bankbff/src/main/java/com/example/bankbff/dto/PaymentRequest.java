package com.example.bankbff.dto;

import java.math.BigDecimal;

public record PaymentRequest(String accountId, BigDecimal amount, String payee) {
}
