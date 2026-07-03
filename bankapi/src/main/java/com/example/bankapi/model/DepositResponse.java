package com.example.bankapi.model;

import java.math.BigDecimal;

public record DepositResponse(String depositReferenceId, BigDecimal amount, String status) {
}
