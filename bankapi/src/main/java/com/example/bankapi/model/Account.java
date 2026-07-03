package com.example.bankapi.model;

import java.math.BigDecimal;

public record Account(
        Long accountId,           // e.g. "1001"
        String accountNumber,     // e.g. "ACC123456"
        String customerId,   // e.g. "C001" -- matches the customer's sub claim
        String accountType,  // "CHECKING", "SAVINGS"
        BigDecimal balance
) {}