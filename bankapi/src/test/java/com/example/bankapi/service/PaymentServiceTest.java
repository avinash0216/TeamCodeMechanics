package com.example.bankapi.service;

import com.example.bankapi.model.PaymentRequest;
import org.junit.jupiter.api.Test;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentServiceTest {

    @Test
    void submitPayment_MapsSuccessfulMockResponse() {
        List<ClientRequest> requests = new ArrayList<>();
        ExchangeFunction exchangeFunction = request -> {
            requests.add(request);
            return Mono.just(ClientResponse.create(HttpStatus.CREATED)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .body("{\"status\":\"ACCEPTED\",\"confirmation\":\"PMT-1001\"}")
                    .build());
        };

        PaymentService service = new PaymentService(
                WebClient.builder()
                        .exchangeFunction(exchangeFunction)
                        .build()
        );

        ResponseEntity<JsonNode> response = service.submitPayment(
                new PaymentRequest("ACC-1", new BigDecimal("50.00"), "Utility"),
                "idem-1001"
        );

        assertThat(requests).hasSize(1);
        assertThat(requests.get(0).url().getPath()).isEqualTo("/payments");
        assertThat(requests.get(0).headers().getFirst("Idempotency-Key")).isEqualTo("idem-1001");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("status").asText()).isEqualTo("ACCEPTED");
        assertThat(response.getBody().get("confirmation").asText()).isEqualTo("PMT-1001");
    }

    @Test
    void submitPayment_PropagatesErrorResponse() {
        ExchangeFunction exchangeFunction = request -> Mono.just(ClientResponse.create(HttpStatus.SERVICE_UNAVAILABLE)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body("{\"status\":\"UNAVAILABLE\"}")
                .build());

        PaymentService service = new PaymentService(
                WebClient.builder()
                        .exchangeFunction(exchangeFunction)
                        .build()
        );

        ResponseEntity<JsonNode> response = service.submitPayment(
                new PaymentRequest("ACC-1", new BigDecimal("999.99"), "Utility"),
                "idem-1002"
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("status").asText()).isEqualTo("UNAVAILABLE");
    }
}
