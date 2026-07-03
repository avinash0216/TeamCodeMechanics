package com.example.bankapi.model;

import java.math.BigDecimal;

public record PaymentResponse(String paymentReferenceId, BigDecimal amount, String status) {
}
