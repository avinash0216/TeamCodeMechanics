package com.example.bankbff.dto;

import java.math.BigDecimal;

public record WithdrawalRequest(String accountNumber, BigDecimal amount) {
}
