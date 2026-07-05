package com.example.bankapi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class PaymentClientConfig {

    @Value("${payment.mock.base-url:http://localhost:8090}")
    private String paymentMockBaseUrl;

    @Bean(name = "paymentMockWebClient")
    public WebClient paymentMockWebClient() {
        return WebClient.builder()
                .baseUrl(paymentMockBaseUrl)
                .build();
    }
}
