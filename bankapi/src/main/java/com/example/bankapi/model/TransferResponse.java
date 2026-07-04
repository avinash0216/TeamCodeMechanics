package com.example.bankapi.model;

import com.example.bankapi.model.enums.TransactionStatus;

public record TransferResponse(
        String transferId,
        Transaction debitTransaction,
        Transaction creditTransaction,
        TransactionStatus status
) {}