package bootcamp.integrations.camel.eip;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class CamelEIPRoutingExample extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("file:/tmp/camel?recursive=true&noop=true")
                .to("direct:consumer");

        from("direct:consumer")
                .routeId("consumer")
                .split(body().tokenize("-"))
                .parallelProcessing()
                .to("direct:reverse");

        from("direct:reverse")
                .routeId("reverse")
                .log("Preparing for reversal: ${body}")
                .setHeader(Exchange.HTTP_METHOD, constant("GET"))
                .enrich().simple("http://localhost:9090/reverse/${body}")
                .log("Received from reversal: ${body}")
                .end();
    }
}
