package bootcamp.integrations;

import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;

@SpringBootApplication
public class BootcampIntegration {
    public static org.slf4j.Logger logger = LoggerFactory.getLogger(BootcampIntegration.class);

    @Autowired
    private KafkaTemplate<String, String> template;

    @Produce(uri = "direct:consumer")
    private ProducerTemplate producerTemplate;

    public static void main(String[] args) throws Exception {
        SpringApplication.run(BootcampIntegration.class, args);
    }

    @KafkaListener(topics = "test")
    public void listen(ConsumerRecord<?, ?> cr) throws Exception {
        logger.info("Received from topic 'test': {}", cr.value());

        // simple route invocation as fire and forget.
        producerTemplate.asyncSendBody(producerTemplate.getDefaultEndpoint(), cr.value());

        // in case you want to have a sync call you can use 'producerTemplate.requestBody'.
        // in that order your kafka listener thread will be blocked until you receive a response from camel route.


        logger.info("!! FINITA LA COMMEDIA !!");
    }
}