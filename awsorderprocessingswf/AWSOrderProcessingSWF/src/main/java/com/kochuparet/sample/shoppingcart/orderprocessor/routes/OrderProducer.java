package com.kochuparet.sample.shoppingcart.orderprocessor.routes;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.aws.swf.SWFConstants;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.kochuparet.sample.shoppingcart.orderprocessor.AwsOrderProcessingSwfApplication;
import com.kochuparet.sample.shoppingcart.orderprocessor.dto.OrderDTO;

@Component
public class OrderProducer extends RouteBuilder {
	
	private static Logger log = LoggerFactory.getLogger(OrderProducer.class);

	@Override
	public void configure() throws Exception {
		from("direct:invokeWorkflow")
				.setHeader(SWFConstants.WORKFLOW_ID, simple("{{wf.name}}"))
                .setHeader("orderNumber", simple("${body.orderNumber}"))
                .marshal()
				.json(JsonLibrary.Jackson, OrderDTO.class)
				.to("aws-swf://workflow?{{wf.route.commonOptions}}&eventName={{wf.name}}")
				.log("SENT WORKFLOW TASK ${body} ${headers}")
				.setHeader("wfRunId", simple("${header[CamelSWFRunId]}"))
				.log("${headers}")
				.process(new Processor() {
					
					@Override
					public void process(Exchange exchange) throws Exception {
						System.out.println(exchange.getIn().getHeaders());
						String orderId = (String) exchange.getIn().getHeader("orderNumber");
						String runId = (String) exchange.getIn().getHeader("wfRunId");

						AwsOrderProcessingSwfApplication.orderIdRunIdMapping.put(orderId, runId);
					}
				})
				;
		

	}
}
