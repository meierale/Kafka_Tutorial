package org.meierale.kafkademoconsumer.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component

public class KafkaConsumer {

    @KafkaListener(
            topics = "KafkaDemo-Topic",
            groupId = "KafkaDemo-Consumer")
    public void
    consume(String message)
    {
        // Just print the message
        System.out.println("message = " + message);
    }
}
