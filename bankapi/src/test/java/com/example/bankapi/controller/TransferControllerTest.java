package com.example.bankapi.controller;

import com.example.bankapi.model.enums.TransactionStatus;
import com.example.bankapi.model.TransactionSummary;
import com.example.bankapi.model.TransferRequest;
import com.example.bankapi.model.TransferResponse;
import com.example.bankapi.service.TransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransferController.class)
@AutoConfigureMockMvc(addFilters = false)
class TransferControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TransactionService transactionService;

    @Test
    void transfer_ReturnsTransferResponse() throws Exception {
        TransferRequest request = new TransferRequest("123456789012", "123456789013", new BigDecimal("25.00"), "test transfer");
        
        TransactionSummary debitTxn = new TransactionSummary("D-1", "123456789012", new BigDecimal("25.00"), Instant.now(), "TRANSFER_OUT", "COMPLETED");
        TransactionSummary creditTxn = new TransactionSummary("C-1", "123456789013", new BigDecimal("25.00"), Instant.now(), "TRANSFER_IN", "COMPLETED");
        TransferResponse response = new TransferResponse("T-1", debitTxn, creditTxn, TransactionStatus.COMPLETE);

        when(transactionService.transfer(request)).thenReturn(response);

        mockMvc.perform(post("/api/v1/transfers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transferId").value("T-1"))
                .andExpect(jsonPath("$.debitTransaction.transactionId").value("D-1"))
                .andExpect(jsonPath("$.creditTransaction.transactionId").value("C-1"))
                .andExpect(jsonPath("$.status").value("COMPLETE"));

        ArgumentCaptor<TransferRequest> captor = ArgumentCaptor.forClass(TransferRequest.class);
        verify(transactionService).transfer(captor.capture());
        assertThat(captor.getValue().fromAccountNumber()).isEqualTo("123456789012");
        assertThat(captor.getValue().toAccountNumber()).isEqualTo("123456789013");
        assertThat(captor.getValue().amount()).isEqualByComparingTo("25.00");
    }
}
