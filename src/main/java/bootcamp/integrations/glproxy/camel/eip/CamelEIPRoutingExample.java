package bootcamp.integrations.glproxy.camel.eip;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class CamelEIPRoutingExample extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("file:/tmp/input/camel?recursive=true&noop=true")
                .to("direct:consumer");

        from("direct:consumer")
                .routeId("consumer")
                .split(body().tokenize("-"))
                //.parallelProcessing()
                .log("Consumed: ${body}")
                .to("direct:reverse");
//                .choice()
//                    .when(body().contains("john"))
//                        .transform(body().prepend("Hello "))
//                        .to("stream:out")
//                        .endChoice()
//                    .when(body().contains("steve"))
//                        .transform(body().prepend("Bye "))
//                        .to("stream:out")
//                        .endChoice()
//                    .otherwise()
//                        .to("direct:reverse");

        from("direct:reverse")
                .routeId("reverse")
                .log("Preparing for reversal: ${body}")
                .setHeader(Exchange.HTTP_METHOD, constant("GET"))
                .enrich().simple("http://localhost:9090/reverse/${body}")
                .log("Received from reversal: ${body}")
                .end();
    }
}
