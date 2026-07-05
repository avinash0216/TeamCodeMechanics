package com.example.bankapi.kafka;

import com.example.bankapi.model.TransactionMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import java.util.concurrent.CompletableFuture;

@Component
public class TransactionPublisher {

    private static final Logger log = LoggerFactory.getLogger(TransactionPublisher.class);
    private static final String TOPIC = "transaction-stats";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public TransactionPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(TransactionMessage transactionMessage) {
        CompletableFuture<SendResult<String, Object>> future =
                kafkaTemplate.send(TOPIC,transactionMessage.type(), transactionMessage);

        future.whenComplete((result, ex) -> {
            if (ex != null) {
                            log.error("Failed to publish transaction message {}",
                                    transactionMessage, ex);
                        } else {
                            var metadata = result.getRecordMetadata();
                            log.info("Published transaction message | type: {} | amount: {} -> partition {}, offset {}",
                                    transactionMessage.type(),
                                    transactionMessage.amount(),
                                    metadata.partition(),
                                    metadata.offset());
                        }
        });
    }
}
