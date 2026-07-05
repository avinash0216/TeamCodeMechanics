package com.example.bankbff.dto;

import java.math.BigDecimal;

public record PaymentRequest(String accountNumber, BigDecimal amount, String payee) {
}
