package com.example.bank.bff.client;

import com.example.bank.bff.dto.AccountDto;
import com.example.bank.bff.dto.TransferRequestDto;
import com.example.bank.bff.dto.TransferResponseDto;
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
                .uri("/api/v1/accounts")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<AccountDto>>() {})
                .block();
    }

    public TransferResponseDto postTransfer(TransferRequestDto request) {
        return bankApiWebClient.post()
                .uri("/api/v1/transfers")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(TransferResponseDto.class)
                .block();
    }
}