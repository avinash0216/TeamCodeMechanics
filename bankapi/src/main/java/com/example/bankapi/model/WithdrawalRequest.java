package com.example.bankapi.model;

import java.math.BigDecimal;

public record WithdrawalRequest(String accountNumber, BigDecimal amount) {
}
