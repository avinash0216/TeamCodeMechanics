package com.example.bankbff.client;

import com.example.bankbff.dto.*;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Component
public class BankingApiClient {

    private final WebClient bankApiWebClient;

    public BankingApiClient(WebClient bankApiWebClient) {
        this.bankApiWebClient = bankApiWebClient;
    }

    public List<AccountDto> getAccounts() {
        return bankApiWebClient.get()
                .uri("/api/v1/accounts/accounts")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<AccountDto>>() {})
                .block();
    }

    public TransferResponseDto postTransfer(TransferRequestDto request) {
        return bankApiWebClient.post()
                .uri("/api/v1/transfers/transfers")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(TransferResponseDto.class)
                .block();
    }

    public PaymentResponseDto postPayment(PaymentRequestDto request) {
        return bankApiWebClient.post()
                .uri("/api/v1/payments")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(PaymentResponseDto.class)
                .block();
    }

    public DepositResponseDto postDeposit(DepositRequestDto request) {
        return bankApiWebClient.post()
                .uri("/api/v1/deposits")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(DepositResponseDto.class)
                .block();
    }
}