package com.example.bankapi.event;

import com.example.bankapi.kafka.TransactionPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class TransactionEventListener {
    
    private final TransactionPublisher transactionPublisher;

    public TransactionEventListener(TransactionPublisher transactionPublisher) {
        this.transactionPublisher = transactionPublisher;
    }

    @EventListener
    public void onTransactionCreated(TransactionCreatedEvent event) {
        transactionPublisher.publish(event.getTransactionMessage());
    }
}
