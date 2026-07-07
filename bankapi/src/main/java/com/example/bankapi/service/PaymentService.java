package com.example.bankapi.service;

import com.example.bankapi.entity.IdempotencyKey;
import com.example.bankapi.exception.DuplicateIdempotencyKeyException;
import com.example.bankapi.exception.MalformedRequestException;
import com.example.bankapi.model.PaymentRequest;
import com.example.bankapi.model.WithdrawalRequest;
import com.example.bankapi.repository.IdempotencyKeyRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
public class PaymentService {

    private static final String IDEMPOTENCY_KEY_HEADER = "Idempotency-Key";

    private final TransactionService transactionService;
    private final IdempotencyKeyRepository idempotencyKeyRepository;
    private final WebClient paymentMockWebClient;

    public PaymentService(TransactionService transactionService,
                          IdempotencyKeyRepository idempotencyKeyRepository,
                          @Qualifier("paymentMockWebClient") WebClient paymentMockWebClient) {
        this.transactionService = transactionService;
        this.idempotencyKeyRepository = idempotencyKeyRepository;
        this.paymentMockWebClient = paymentMockWebClient;
    }

    @Transactional
    public ResponseEntity<JsonNode> submitPayment(PaymentRequest request, String idempotencyKey) {
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            throw new MalformedRequestException("MISSING_FIELD", IDEMPOTENCY_KEY_HEADER, "Idempotency-Key header is required");
        }

        // Check if idempotency key already exists (before circuit breaker logic)
        if (idempotencyKeyRepository.existsById(idempotencyKey)) {
            throw new DuplicateIdempotencyKeyException(idempotencyKey);
        }

        // Process payment with circuit breaker protection
        ResponseEntity<JsonNode> response = processPaymentWithCircuitBreaker(request, idempotencyKey);

        // Save idempotency key after successful transaction
        idempotencyKeyRepository.save(new IdempotencyKey(idempotencyKey));

        return response;
    }

    @CircuitBreaker(name = "paymentApi", fallbackMethod = "paymentApiFallback")
    private ResponseEntity<JsonNode> processPaymentWithCircuitBreaker(PaymentRequest request, String idempotencyKey) {
        // Process payment: debit account
        transactionService.withdrawFromAccount(new WithdrawalRequest(request.accountNumber(), request.amount()));

        // Call downstream payment service
        return paymentMockWebClient.post()
                .uri("/payments")
                .header(IDEMPOTENCY_KEY_HEADER, idempotencyKey)
                .bodyValue(request)
                .retrieve()
                .onStatus(status -> status.is5xxServerError(),
                        resp -> resp.createException().flatMap(Mono::error))
                .toEntity(JsonNode.class)
                .map(resp -> ResponseEntity.status(resp.getStatusCode())
                        .contentType(resp.getHeaders().getContentType() != null
                                ? resp.getHeaders().getContentType()
                                : MediaType.APPLICATION_JSON)
                        .body(resp.getBody() != null ? resp.getBody() : JsonNodeFactory.instance.nullNode()))
                .timeout(Duration.ofSeconds(3))
                .block();
    }

    private ResponseEntity<JsonNode> paymentApiFallback(PaymentRequest request, String idempotencyKey, Throwable ex) {
        ObjectNode body = JsonNodeFactory.instance.objectNode();
        body.put("status", "UNAVAILABLE");
        body.put("errorCode", "PAYMENT_PROVIDER_UNAVAILABLE");
        body.put("message", "Payment service is temporarily unavailable");
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body);
    }
}
