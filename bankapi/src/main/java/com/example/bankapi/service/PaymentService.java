package com.example.bankapi.service;

import com.example.bankapi.exception.MalformedRequestException;
import com.example.bankapi.model.PaymentRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
public class PaymentService {

    private static final String IDEMPOTENCY_KEY_HEADER = "Idempotency-Key";

    private final WebClient paymentMockWebClient;

    public PaymentService(@Qualifier("paymentMockWebClient") WebClient paymentMockWebClient) {
        this.paymentMockWebClient = paymentMockWebClient;
    }

    @CircuitBreaker(name = "paymentApi", fallbackMethod = "paymentApiFallback")
    public ResponseEntity<JsonNode> submitPayment(PaymentRequest request, String idempotencyKey) {
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            throw new MalformedRequestException("MISSING_FIELD", IDEMPOTENCY_KEY_HEADER, "Idempotency-Key header is required");
        }
        return paymentMockWebClient.post()
                .uri("/payments")
                .header(IDEMPOTENCY_KEY_HEADER, idempotencyKey)
                .bodyValue(request)
                .retrieve()
                .onStatus(status -> status.is5xxServerError(),
                        response -> response.createException().flatMap(Mono::error))
                .toEntity(JsonNode.class)
                .map(response -> ResponseEntity.status(response.getStatusCode())
                        .contentType(response.getHeaders().getContentType() != null
                                ? response.getHeaders().getContentType()
                                : MediaType.APPLICATION_JSON)
                        .body(response.getBody() != null ? response.getBody() : JsonNodeFactory.instance.nullNode()))
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
