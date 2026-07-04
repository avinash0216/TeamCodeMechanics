package com.example.bankbff.dto;

import com.example.bankbff.dto.enums.TransactionStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TransferResponse(
        String transferId,
        Transaction debitTransaction,
        Transaction creditTransaction,
        TransactionStatus status
) {}
