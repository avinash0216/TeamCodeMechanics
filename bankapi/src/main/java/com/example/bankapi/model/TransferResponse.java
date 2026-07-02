package com.example.bankapi.model;

public record TransferResponse(
        String debitTransactionId,
        String creditTransactionId,
        TransactionStatus status
) {}