package com.example.bank.bff.controller;

import com.example.bank.bff.client.BankingApiClient;
import com.example.bank.bff.dto.AccountDto;
import com.example.bank.bff.dto.TransferRequestDto;
import com.example.bank.bff.dto.TransferResponseDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AccountController {

    private final BankingApiClient bankingApiClient;

    public AccountController(BankingApiClient bankingApiClient) {
        this.bankingApiClient = bankingApiClient;
    }

    @GetMapping("/accounts")
    public List<AccountDto> accounts() {
        return bankingApiClient.getAccounts();
    }

    @PostMapping("/transfers")
    public TransferResponseDto transfer(@RequestBody TransferRequestDto request) {
        return bankingApiClient.postTransfer(request);
    }
}