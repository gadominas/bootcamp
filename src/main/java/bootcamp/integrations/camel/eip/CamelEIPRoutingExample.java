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
                /* enabling parallel processing afterwards
                 * for each token to go further in async way.
                 * You will notice this in stdout as records will
                 * appear in non deterministic order.
                 * */
                .parallelProcessing()
                .log("Consumed: ${body}")
                /* that's is the simple choice, aka: content router */
                .choice()
                    .when(body().contains("john"))
                        .transform(body().prepend("Hello "))
                        .to("stream:out")
                        .endChoice()
                    .when(body().contains("steve"))
                        .transform(body().prepend("Bye "))
                        .to("stream:out")
                        .endChoice()
                    .otherwise()
                        /* In case token is not steve or john, token
                         * is send to reverse route for str reverse through rest */
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
