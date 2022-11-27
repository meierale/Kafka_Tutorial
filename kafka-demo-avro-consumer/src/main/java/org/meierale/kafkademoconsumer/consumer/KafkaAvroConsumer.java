package org.meierale.kafkademoconsumer.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import lombok.extern.apachecommons.CommonsLog;
import org.meierale.Customer;

@Component

@CommonsLog(topic = "Consumer Logger")
public class KafkaAvroConsumer {

    @KafkaListener(
        topics = "${topic.name}",
        groupId = "${spring.kafka.consumer.group-id}",
        properties = {
                "auto.offset.reset=earliest"
        })
    public void consume(ConsumerRecord<String, Customer> record)
    {
        log.debug("Processing trigger event from notification topic");
        log.info(String.format("Consumed message -> %s", record.value()));
        Customer customer = record.value();
        System.out.println(String.format(
                "Customer:  lastName= %s, firstName= %s, age= %d",
                customer.getLastName(),
                customer.getFirstName(),
                customer.getAge())
        );
    }
}
