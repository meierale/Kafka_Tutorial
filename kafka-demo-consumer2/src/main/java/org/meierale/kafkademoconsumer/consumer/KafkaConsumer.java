package org.meierale.kafkademoconsumer.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component

public class KafkaConsumer {

    @KafkaListener(
            topics = "${topic.name}",
            groupId = "${spring.kafka.consumer.group-id}",
            properties = {
                    "auto.offset.reset=earliest"
            })
    public void consume(String message)
    {
        // Just print the message
        System.out.println("message = " + message);
    }
}
