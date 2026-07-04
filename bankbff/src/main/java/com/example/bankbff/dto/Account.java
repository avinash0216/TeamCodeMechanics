package com.example.bankbff.dto;

import java.math.BigDecimal;

/**
 * Account as returned from bankapi.
 *
 * Field names match the JSON exactly: accountNumber, accountNumber, customerId, accountType, balance.
 */
public record Account(
        Long accountId,
        String accountNumber,
        String customerId,
        String accountType,
        BigDecimal balance
) {}