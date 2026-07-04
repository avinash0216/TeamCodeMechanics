package com.example.bankbff.dto;

import java.math.BigDecimal;

public record DepositRequest(String accountNumber, BigDecimal amount) {
}
