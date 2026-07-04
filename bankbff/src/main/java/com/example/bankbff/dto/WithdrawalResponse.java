package com.example.bankbff.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record WithdrawalResponse(String transactionId,
                                 String accountNumber,
                                 BigDecimal amount,
                                 Instant timestamp,
                                 String status
) {}
