package bootcamp.integrations.glproxy;

import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.ProducerFactory;

@Configuration
@EnableKafka
public class Config {
    @Bean
    public ProducerFactory<?, ?> kafkaProducerFactory(KafkaProperties properties) {
        DefaultKafkaProducerFactory<Object, Object> pf =
                new DefaultKafkaProducerFactory(properties.buildProducerProperties());

        pf.setTransactionIdPrefix("txn-ingestor");
        return pf;
    }
}