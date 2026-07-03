package com.example.bankbff.dto;

import java.math.BigDecimal;

public record WithdrawalRequestDto(String accountId, BigDecimal amount) {
}
