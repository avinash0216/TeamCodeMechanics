package com.example.bankbff.dto;

import java.math.BigDecimal;

public record WithdrawalResponseDto(String withdrawalReferenceId, BigDecimal amount, String status) {
}
