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

    public List<Account> getAccounts() {
        return bankApiWebClient.get()
                .uri("/api/v1/accounts/accounts")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Account>>() {})
                .block();
    }

    public TransferResponse postTransfer(TransferRequest request) {
        return bankApiWebClient.post()
                .uri("/api/v1/transfers/transfers")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(TransferResponse.class)
                .block();
    }

    public PaymentResponse postPayment(PaymentRequest request) {
        return bankApiWebClient.post()
                .uri("/api/v1/payments/payments")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(PaymentResponse.class)
                .block();
    }

    public DepositResponse postDeposit(DepositRequest request) {
        return bankApiWebClient.post()
                .uri("/api/v1/deposits/deposits")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(DepositResponse.class)
                .block();
    }

    public WithdrawalResponse postWithdrawal(WithdrawalRequest request) {
        return bankApiWebClient.post()
                .uri("/api/v1/withdrawals/withdrawals")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(WithdrawalResponse.class)
                .block();
    }
}