package bootcamp.integrations.camel;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;


@Component
public class RestRouteBuilder extends RouteBuilder {
    public static org.slf4j.Logger logger = LoggerFactory.getLogger(RestRouteBuilder.class);

    @Autowired
    private KafkaTemplate<String, String> template;

    @Override
    public void configure() throws Exception {
        /* Simple CAMEL-REST config */
        restConfiguration()
                .component("restlet")
                .host("localhost")
                .port("9090")
                .bindingMode(RestBindingMode.json)
                .dataFormatProperty("prettyPrint", "true");


        rest("/reverse").description("Reversing provided text")
                .consumes("application/json").description("Consumes json")
                .produces("application/json").description("Produce json")
                .get("/{in}").description("Takes text as an argument")
                .outType(String.class).description("Produce string type response")
                .route()
                .routeId("rest-reverse")
                .process((exchange) -> {
                    String reversedInput = (String) exchange.getIn().getHeader("in");
                    exchange.getOut().setBody(new StringBuilder(reversedInput).reverse().toString());
                })
                .endRest();

        rest("/topic")
                .consumes("application/json")
                .produces("application/json")
                .post("/{url}")
                .outType(String.class)
                .route()
                .routeId("rest-topic")
                .log("Sending to topic: ${header.url}")
                .setProperty("urlIn", simple("${header.url}"))
                .process((exchange) -> {
                    boolean results = template.executeInTransaction(t -> {
                        ProducerRecord<String, String> record =
                                new ProducerRecord<String, String>("test1", (String) exchange.getIn().getHeader("url"));

                        ListenableFuture<SendResult<String, String>> future = template.send(record);

                        future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
                            @Override
                            public void onSuccess(SendResult<String, String> result) {
                                logger.info("Message: {} was successfully sent.", result.toString());
                            }

                            @Override
                            public void onFailure(Throwable ex) {
                                logger.error("Message failed to send due to: {}", ex);
                            }
                        });

                        return true;
                    });
                })
                .setBody(simple("${property.urlIn} message was send!"))
                .endRest();
    }
}
