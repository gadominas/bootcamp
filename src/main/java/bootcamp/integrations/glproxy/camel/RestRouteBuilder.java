package bootcamp.integrations.glproxy.camel;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;


@Component
public class RestRouteBuilder extends RouteBuilder {
    @Autowired
    private KafkaTemplate<String, String> template;

    @Override
    public void configure() throws Exception {
        restConfiguration()
                .host("localhost")
                .port("8080")
                .component("restlet")
                .bindingMode(RestBindingMode.json)
                .dataFormatProperty("prettyPrint", "true");

        rest("/reverse")
                .consumes("application/json")
                .produces("application/json")
                .get("/{in}")
                .outType(String.class)
                .route()
                //.delay(5000L)
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
                .log("Sending to topic: ${header.url}")
                .setProperty("urlIn", simple("${header.url}"))
                .process((exchange) -> {
                    boolean results = template.executeInTransaction(t -> {
                        ProducerRecord<String, String> record =
                                new ProducerRecord<String, String>("test", (String) exchange.getIn().getHeader("url"));
                        template.send(record);

                        return true;
                    });
                })
                .setBody(simple("${property.urlIn} message was send!"))
                .endRest();
    }
}
