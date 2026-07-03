package com.example.bankapi.model;

import java.math.BigDecimal;

public record WithdrawalResponse(String withdrawalReferenceId, BigDecimal amount, String status) {
}
