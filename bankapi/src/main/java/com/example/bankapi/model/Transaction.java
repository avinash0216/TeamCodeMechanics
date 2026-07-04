package com.example.bankapi.model;

import java.math.BigDecimal;
import java.time.Instant;

public sealed interface Transaction
    permits DepositResponse, WithdrawalResponse, TransactionSummary {
    String transactionId();
    String accountNumber();
    BigDecimal amount();
    Instant timestamp();
}