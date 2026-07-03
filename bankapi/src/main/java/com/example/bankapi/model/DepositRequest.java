package com.example.bankapi.model;

import java.math.BigDecimal;

public record DepositRequest(String accountNumber, BigDecimal amount) {
}
