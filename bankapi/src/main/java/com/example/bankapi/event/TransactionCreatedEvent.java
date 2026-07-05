package com.example.bankapi.event;

import com.example.bankapi.model.TransactionMessage;
import org.springframework.context.ApplicationEvent;

public class TransactionCreatedEvent extends ApplicationEvent {
    private final TransactionMessage transactionMessage;

    public TransactionCreatedEvent(Object source, TransactionMessage transactionMessage) {
        super(source);
        this.transactionMessage = transactionMessage;
    }

    public TransactionMessage getTransactionMessage() {
        return transactionMessage;
    }
}
