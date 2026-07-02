package com.example.bank.bff.dto;

public record TransferResponseDto(
        String transactionId,
        String status
) {}