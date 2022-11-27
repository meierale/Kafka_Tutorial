package org.meierale.kafkaavrodemoproducer;

import org.meierale.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import lombok.extern.apachecommons.CommonsLog;

@Service
@CommonsLog(topic = "Producer Logger")
public class AvroProducer {

    @Value("${topic.name}")
    private String TOPIC;

    private final KafkaTemplate<String, Customer> kafkaTemplate;

    @Autowired
    public AvroProducer(KafkaTemplate<String, Customer> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    void sendMessage(Customer customer) {
        log.info(String.format("About to produce customer: %s", customer));
        this.kafkaTemplate.send(this.TOPIC, customer.getLastName(), customer);
        log.info(String.format("Produced customer -> %s", customer));
    }
}