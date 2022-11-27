package org.meierale.kafkaavrodemoproducer;

// Importing required classes
import org.meierale.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.apachecommons.CommonsLog;

// Annotation
@RestController

// Class
@CommonsLog(topic = "Producer Logger")
public class DemoController {

    private final AvroProducer producer;
    // Autowiring Kafka Template
    @Autowired
    DemoController(AvroProducer producer) {
        this.producer = producer;
    }

    // Publish messages using the PostMapping
    @PostMapping("/avro-publish")
    public String publishMessage(@RequestBody Customer customer)
    {

        log.info(String.format("received customer -> %s via POST /avro-publish", customer.getLastName()));
        this.producer.sendMessage(customer);

        return "Avro Customer Published Successfully";
    }
}

