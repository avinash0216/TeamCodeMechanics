package com.example.bankbff.dto;

import java.math.BigDecimal;

public record DepositRequestDto(String accountNumber, BigDecimal amount) {
}
