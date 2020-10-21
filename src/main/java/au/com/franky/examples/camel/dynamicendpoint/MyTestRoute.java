package au.com.franky.examples.camel.dynamicendpoint;

import org.apache.camel.builder.EndpointProducerBuilder;
import org.apache.camel.builder.endpoint.EndpointRouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyTestRoute extends EndpointRouteBuilder {
	private static final String MY_TOPIC = "testTopic";

	@Autowired
	private EndpointProducerBuilder myKafkaBean;

	@Override
	public void configure() {
		from(timer("myTimer").repeatCount(1))
				.setBody(constant("THIS IS A TEST MESSAGE"))
				.setProperty("destination2", constant(MY_TOPIC))
				.to("direct:publish-to-kafka2")
		;

		from("direct:publish-to-kafka2")
				.toD(myKafkaBean)
		;

		from(kafka(MY_TOPIC))
				.log("Received: ${body}")
		;
	}

	@Bean
	public EndpointProducerBuilder myKafkaBean() {
		return kafka("${exchangeProperty.destination2}");
	}
}
