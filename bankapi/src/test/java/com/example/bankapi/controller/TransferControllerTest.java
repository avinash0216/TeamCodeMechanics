package com.example.bankapi.controller;

import com.example.bankapi.model.TransactionStatus;
import com.example.bankapi.model.TransferRequest;
import com.example.bankapi.model.TransferResponse;
import com.example.bankapi.service.TransferService;
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
    private TransferService transferService;

    @Test
    void transfer_ReturnsTransferResponse() throws Exception {
        TransferRequest request = new TransferRequest(1001L, 1002L, new BigDecimal("25.00"), "test transfer");
        TransferResponse response = new TransferResponse("D-1", "C-1", TransactionStatus.COMPLETE);

        when(transferService.transferBetweenAccountsSameCustomer(request)).thenReturn(response);

        mockMvc.perform(post("/api/v1/transfers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.debitTransactionId").value("D-1"))
                .andExpect(jsonPath("$.creditTransactionId").value("C-1"))
                .andExpect(jsonPath("$.status").value("COMPLETE"));

        ArgumentCaptor<TransferRequest> captor = ArgumentCaptor.forClass(TransferRequest.class);
        verify(transferService).transferBetweenAccountsSameCustomer(captor.capture());
        assertThat(captor.getValue().fromAccountId()).isEqualTo(1001L);
        assertThat(captor.getValue().toAccountId()).isEqualTo(1002L);
        assertThat(captor.getValue().amount()).isEqualByComparingTo("25.00");
    }
}
