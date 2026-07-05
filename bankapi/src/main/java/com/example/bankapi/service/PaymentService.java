package com.example.bankapi.service;

import com.example.bankapi.model.PaymentRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class PaymentService {

    private final WebClient paymentMockWebClient;

    public PaymentService(@Qualifier("paymentMockWebClient") WebClient paymentMockWebClient) {
        this.paymentMockWebClient = paymentMockWebClient;
    }

    public ResponseEntity<JsonNode> submitPayment(PaymentRequest request) {
        return paymentMockWebClient.post()
                .uri("/payments")
                .bodyValue(request)
                .exchangeToMono(response -> response.bodyToMono(JsonNode.class)
                        .defaultIfEmpty(JsonNodeFactory.instance.nullNode())
                        .map(body -> ResponseEntity.status(response.statusCode())
                                .contentType(response.headers().contentType().orElse(MediaType.APPLICATION_JSON))
                                .body(body)))
                .block();
    }
}
