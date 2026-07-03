package com.example.bankbff.dto;

import java.math.BigDecimal;

public record PaymentResponseDto(String paymentReferenceId, BigDecimal amount, String status) {
}
