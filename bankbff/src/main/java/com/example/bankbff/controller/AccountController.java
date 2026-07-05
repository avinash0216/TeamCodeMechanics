package com.example.bankbff.controller;

import com.example.bankbff.client.BankingApiClient;
import com.example.bankbff.dto.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<ApiResponse<List<Account>>> accounts() {
        List<Account> accounts = bankingApiClient.getAccounts();
        ApiResponse<List<Account>> resp = ApiResponse.success(HttpStatus.OK.toString(), "SUCCESS", "", accounts);
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/transfers")
    public ResponseEntity<ApiResponse<TransferResponse>> transfer(@RequestBody TransferRequest request) {
        TransferResponse response = bankingApiClient.postTransfer(request);
        ApiResponse<TransferResponse> resp = ApiResponse.success(HttpStatus.OK.toString(), "SUCCESS", "", response);
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/payments")
    public ResponseEntity<ApiResponse<PaymentResponse>> SubmitPayment(@RequestBody PaymentRequest request) {
        PaymentResponse response = bankingApiClient.postPayment(request);
        ApiResponse<PaymentResponse> resp = ApiResponse.success(HttpStatus.OK.toString(), "SUCCESS", "", response);
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/deposits")
    public ResponseEntity<ApiResponse<DepositResponse>> SubmitDeposit(@RequestBody DepositRequest request) {
        DepositResponse response = bankingApiClient.postDeposit(request);
        ApiResponse<DepositResponse> resp = ApiResponse.success(HttpStatus.OK.toString(), "OK", "", response);
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/withdrawals")
    public ResponseEntity<ApiResponse<WithdrawalResponse>> SubmitWithdrawal(@RequestBody WithdrawalRequest request) {
        WithdrawalResponse response = bankingApiClient.postWithdrawal(request);
        ApiResponse<WithdrawalResponse> resp = ApiResponse.success(HttpStatus.OK.toString(), "SUCCESS", "", response);
        return ResponseEntity.ok(resp);
    }
}