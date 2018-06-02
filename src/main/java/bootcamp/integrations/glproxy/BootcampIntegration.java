package bootcamp.integrations.glproxy;

import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.concurrent.CountDownLatch;

@SpringBootApplication
public class BootcampIntegration implements CommandLineRunner {
    public static org.slf4j.Logger logger = LoggerFactory.getLogger(BootcampIntegration.class);

    private final CountDownLatch latch = new CountDownLatch(1);

    @Autowired
    private KafkaTemplate<String, String> template;

    @Produce(uri = "direct:consumer")
    private ProducerTemplate producerTemplate;

    public static void main(String[] args) {
        SpringApplication springApplication =
                new SpringApplicationBuilder()
                        .sources(BootcampIntegration.class)
                        .web(WebApplicationType.SERVLET)
                        .build();

        springApplication.run(args);
    }

    @Override
    public void run(String... args) throws Exception {
//        boolean results = template.executeInTransaction(t -> {
//            for (int m = 0; m < 100; m++) {
//                ProducerRecord<String, String> record = new ProducerRecord<String, String>("test", "data_" + m);
//                logger.info("Event {} send!", record.value());
//
//                ListenableFuture<SendResult<String, String>> future = template.send(record);
//                future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
//                    @Override
//                    public void onSuccess(@Nullable SendResult<String, String> stringStringSendResult) {
//                        logger.info("OnSuccess: {}", stringStringSendResult.toString());
//                    }
//
//                    @Override
//                    public void onFailure(Throwable ex) {
//                        logger.info("OnFailure: {}", ex.toString());
//                    }
//                });
//            }
//
//            return true;
//        });
    }

    @KafkaListener(topics = "test")
    public void listen(ConsumerRecord<?, ?> cr) throws Exception {
        logger.info("Received: " + cr.value());

        producerTemplate.asyncSendBody(producerTemplate.getDefaultEndpoint(), cr.value());

//        Object results = producerTemplate.requestBody(producerTemplate.getDefaultEndpoint(), cr.value());
//        logger.info("Some:"+results.toString());

        logger.info("!! FINITA LA COMMEDIA !!");
    }
}