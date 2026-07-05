package com.example.bankapi.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record PaymentRequest(
        @NotBlank(message = "Account Number is required")
        String accountNumber,

        @Positive(message = "Payment amount must be positive")
        BigDecimal amount,

        @NotBlank(message = "Payee is required")
        String payee) {
}
