package org.meierale.kafkademoproducer;

// Importing required classes
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

// Annotation
@RestController

// Class
public class DemoController {

    // Autowiring Kafka Template
    @Autowired KafkaTemplate<String, String> kafkaTemplateString;
    @Autowired KafkaTemplate<String, Book> kafkaTemplateBook;

    private static final String TOPIC = "KafkaDemo-Topic";
    private static final String JSON_TOPIC = "KafkaDemo-JSON-Topic";

    // Publish messages using the GetMapping
    @GetMapping("/publish/{message}")
    public String publishMessage(@PathVariable("message")
    final String message)
    {

        // Sending the message
        kafkaTemplateString.send(TOPIC, message);

        return "Plaintext Published Successfully";
    }

    // Publish messages using the PostMapping
    @PostMapping("/publish")
    public String publishMessage(@RequestBody Book book)
    {

        // Sending the message
        kafkaTemplateBook.send(JSON_TOPIC, book);

        return "Book JSON Published Successfully";
    }
}
