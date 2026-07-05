package com.example.bankapi.model;

import java.math.BigDecimal;

public record TransactionMessage(String type, BigDecimal amount) {
}
