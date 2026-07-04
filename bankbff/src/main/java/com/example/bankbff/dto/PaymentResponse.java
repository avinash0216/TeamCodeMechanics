package com.example.bankbff.dto;

import java.math.BigDecimal;

public record PaymentResponse(String paymentReferenceId, BigDecimal amount, String status) {
}
