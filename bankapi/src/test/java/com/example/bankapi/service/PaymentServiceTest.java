package com.example.bankapi.service;

import com.example.bankapi.entity.IdempotencyKey;
import com.example.bankapi.exception.DuplicateIdempotencyKeyException;
import com.example.bankapi.model.PaymentRequest;
import com.example.bankapi.model.WithdrawalResponse;
import com.example.bankapi.repository.IdempotencyKeyRepository;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PaymentServiceTest {

    @Test
    void submitPayment_ThrowsDuplicateIdempotencyKeyException() {
        TransactionService transactionService = mock(TransactionService.class);
        IdempotencyKeyRepository idempotencyKeyRepository = mock(IdempotencyKeyRepository.class);
        when(idempotencyKeyRepository.existsById("duplicate-key-001")).thenReturn(true);

        PaymentService service = new PaymentService(
                transactionService,
                idempotencyKeyRepository,
                WebClient.builder().build()
        );

        assertThatThrownBy(() -> service.submitPayment(
                new PaymentRequest("ACC-1", new BigDecimal("50.00"), "Utility"),
                "duplicate-key-001"
        ))
        .isInstanceOf(DuplicateIdempotencyKeyException.class)
        .hasMessageContaining("Idempotency key already used");
    }

    @Test
    void submitPayment_ThrowsMalformedRequestException_MissingIdempotencyKey() {
        TransactionService transactionService = mock(TransactionService.class);
        IdempotencyKeyRepository idempotencyKeyRepository = mock(IdempotencyKeyRepository.class);

        PaymentService service = new PaymentService(
                transactionService,
                idempotencyKeyRepository,
                WebClient.builder().build()
        );

        assertThatThrownBy(() -> service.submitPayment(
                new PaymentRequest("ACC-1", new BigDecimal("50.00"), "Utility"),
                null
        ))
        .isInstanceOf(com.example.bankapi.exception.MalformedRequestException.class)
        .hasMessageContaining("Idempotency-Key header is required");
    }

    @Test
    void submitPayment_ThrowsMalformedRequestException_BlankIdempotencyKey() {
        TransactionService transactionService = mock(TransactionService.class);
        IdempotencyKeyRepository idempotencyKeyRepository = mock(IdempotencyKeyRepository.class);

        PaymentService service = new PaymentService(
                transactionService,
                idempotencyKeyRepository,
                WebClient.builder().build()
        );

        assertThatThrownBy(() -> service.submitPayment(
                new PaymentRequest("ACC-1", new BigDecimal("50.00"), "Utility"),
                "   "
        ))
        .isInstanceOf(com.example.bankapi.exception.MalformedRequestException.class)
        .hasMessageContaining("Idempotency-Key header is required");
    }

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

        TransactionService transactionService = mock(TransactionService.class);
        when(transactionService.withdrawFromAccount(any())).thenReturn(
                new WithdrawalResponse("txn-1", "ACC-1", new BigDecimal("50.00"), java.time.Instant.now(), "COMPLETED")
        );

        IdempotencyKeyRepository idempotencyKeyRepository = mock(IdempotencyKeyRepository.class);
        when(idempotencyKeyRepository.existsById("idem-1001")).thenReturn(false);

        PaymentService service = new PaymentService(
                transactionService,
                idempotencyKeyRepository,
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
        verify(transactionService).withdrawFromAccount(any());
        verify(idempotencyKeyRepository).save(any(IdempotencyKey.class));
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

        TransactionService transactionService = mock(TransactionService.class);
        when(transactionService.withdrawFromAccount(any())).thenReturn(
                new WithdrawalResponse("txn-2", "ACC-1", new BigDecimal("999.99"), java.time.Instant.now(), "COMPLETED")
        );

        IdempotencyKeyRepository idempotencyKeyRepository = mock(IdempotencyKeyRepository.class);
        when(idempotencyKeyRepository.existsById("idem-1002")).thenReturn(false);

        PaymentService service = new PaymentService(
                transactionService,
                idempotencyKeyRepository,
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
