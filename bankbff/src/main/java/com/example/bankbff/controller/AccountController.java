package com.example.bankbff.controller;

import com.example.bankbff.client.BankingApiClient;
import com.example.bankbff.dto.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
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


    @PostMapping("/payments")
    public PaymentResponseDto SubmitPayment(@RequestBody PaymentRequestDto request) {
        // Implement the logic to submit a payment
        return new PaymentResponseDto("Payment submitted successfully", BigDecimal.valueOf(123456789L), "SUCCESS");
    }

    @PostMapping("/deposits")
    public DepositResponseDto SubmitDeposit(@RequestBody DepositRequestDto request) {
        // Implement the logic to submit a deposit
        return new DepositResponseDto("Deposit submitted successfully", BigDecimal.valueOf(123456789L), "SUCCESS");
    }

    @PostMapping("/withdrawals")
    public WithdrawalResponseDto SubmitWithdrawal(@RequestBody WithdrawalRequestDto request) {
        // Implement the logic to submit a withdrawal
        return new WithdrawalResponseDto("Withdrawal submitted successfully", BigDecimal.valueOf(123456789L), "SUCCESS");
    }
}