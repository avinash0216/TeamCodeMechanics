package com.example.bankbff.dto;

import java.math.BigDecimal;

public record DepositResponseDto(String depositReferenceId, BigDecimal amount, String status) {
}
