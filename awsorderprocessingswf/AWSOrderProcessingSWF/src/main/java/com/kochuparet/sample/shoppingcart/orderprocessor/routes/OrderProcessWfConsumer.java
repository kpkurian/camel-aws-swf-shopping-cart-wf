package com.kochuparet.sample.shoppingcart.orderprocessor.routes;

import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.aws.swf.CamelSWFWorkflowClient;
import org.apache.camel.component.aws.swf.SWFConstants;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.amazonaws.services.simpleworkflow.model.ActivityTaskCompletedEventAttributes;
import com.amazonaws.services.simpleworkflow.model.HistoryEvent;
import com.kochuparet.sample.shoppingcart.orderprocessor.AwsOrderProcessingSwfApplication;
import com.kochuparet.sample.shoppingcart.orderprocessor.dto.OrderDTO;
import com.kochuparet.sample.shoppingcart.orderprocessor.service.VerifyOrderProcessor;

@Component
public class OrderProcessWfConsumer extends RouteBuilder {
	
	@Value("${wf.name}")
	private String wfName;
	

    @Override
    public void configure() throws Exception {

        from("aws-swf://workflow?{{wf.route.commonOptions}}&eventName={{wf.name}}")
        			.log("Message received  header==${headers} == body == ${body}")
                .choice()
                    .when(header(SWFConstants.ACTION).isEqualTo(SWFConstants.SIGNAL_RECEIVED_ACTION))
                        .log("Signal received ${body}")

                    .when(header(SWFConstants.ACTION).isEqualTo(SWFConstants.GET_STATE_ACTION))
                        .log("State asked ${body} ${headers}")

                    .when(header(SWFConstants.ACTION).isEqualTo(SWFConstants.EXECUTE_ACTION))
                        .setBody(simple("${body[0]}"))
                        .unmarshal()
                        .json(JsonLibrary.Jackson, OrderDTO.class)
                        .log( "EXECUTION TASK RECEIVED ${body} ${headers}")
                        .setHeader(SWFConstants.WORKFLOW_ID, simple(wfName))
                        .process(new Processor() {
							
							@Override
							public void process(Exchange exchange) throws Exception {
								String orderNumber = exchange.getIn().getBody(OrderDTO.class).getOrderNumber();
								exchange.getOut().getHeaders().putAll(exchange.getIn().getHeaders());
								exchange.getOut().setHeader(SWFConstants.RUN_ID, AwsOrderProcessingSwfApplication.orderIdRunIdMapping.get(orderNumber));
								exchange.getOut().setHeader(SWFConstants.WORKFLOW_ID, wfName);
								Boolean replay = (Boolean)exchange.getIn().getHeader(SWFConstants.WORKFLOW_REPLAYING);
								if(replay) {
									exchange.getOut().setHeader("wfRoute", "decisionWfProcessor");
								} else {
									exchange.getOut().setHeader("wfRoute", "startWfProcessor");
								}
								exchange.getOut().setBody(exchange.getIn().getBody());
								
							}
						})
                        .choice()
                    			.when(header("wfRoute").isEqualTo(constant("decisionWfProcessor")))
					            .setHeader(SWFConstants.OPERATION, constant("GET_HISTORY"))
					            .to("aws-swf://workflow?{{wf.route.commonOptions}}&operation=GET_HISTORY&eventName={{wf.name}}")
					            .log("Fetching HISTORY ${body} ${headers}")
		                        .setBody(simple("${body}"))
		                        .process(new Processor() {
									
									@Override
									public void process(Exchange exchange) throws Exception {								
										exchange.getOut().getHeaders().putAll(exchange.getIn().getHeaders());
										exchange.getOut().setBody(exchange.getIn().getBody());
										
									}
								})
	                            .to("direct:decideNextStep")
                            .when(header("wfRoute").isEqualTo(constant("startWfProcessor")))
		                        .marshal()
		                        .json(JsonLibrary.Jackson, OrderDTO.class)
		                        .process(new Processor() {
									
									@Override
									public void process(Exchange exchange) throws Exception {	
										exchange.getOut().getHeaders().putAll(exchange.getIn().getHeaders());
										exchange.getOut().setHeader("status", "Started");
										exchange.getOut().setBody(exchange.getIn().getBody());
										
									}
								})
	                            .to("direct:startFirstStep")
                            .otherwise()
                            		.log("ENDING WORKFLOW AS STATUS IS NOT STARTED OR INPROGRESS")
					            ;;

				
    }
}

