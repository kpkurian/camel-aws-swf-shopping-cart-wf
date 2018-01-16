package com.kochuparet.sample.shoppingcart.orderprocessor.routes;

import static com.amazonaws.services.simpleworkflow.model.EventType.ActivityTaskCompleted;
import static com.amazonaws.services.simpleworkflow.model.EventType.ActivityTaskFailed;

import java.util.Base64;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.aws.swf.SWFConstants;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.amazonaws.services.simpleworkflow.model.EventType;
import com.amazonaws.services.simpleworkflow.model.HistoryEvent;
import com.kochuparet.sample.shoppingcart.orderprocessor.dto.OrderDTO;

@Component
public class NextStepDecisionRoute extends RouteBuilder {
	
	@Value("${wf.name}")
	private String wfName;

	@Override
	public void configure() throws Exception {
		from("direct:startFirstStep")
			.setBody(simple("${body}"))
			.log("==========>>>Received startFirstStep ${body}")
			.log("********* TO VERIFY ORDER")
//			.marshal()
//			.json(JsonLibrary.Jackson, OrderDTO.class)
			.setHeader(SWFConstants.EVENT_NAME, constant("VerifyOrder"))
			.setHeader(SWFConstants.VERSION, constant("3.0"))
			.to("aws-swf://activity?{{wf.route.commonOptions}}&eventName=VerifyOrder")
			.log("SENT TO VERIFY ORDER ${body} ${headers}");
		
		from("direct:decideNextStep")
			.setBody(simple("${body}"))
			.log("==========>>>Received decideNextStep ${body}")
			.process(new Processor() {
				
				@Override
				public void process(Exchange exchange) throws Exception {
					Object eventsObj =  exchange.getIn().getBody();
					System.out.println(eventsObj);
					if(eventsObj != null  && eventsObj instanceof List) {
						List<HistoryEvent> events = (List<HistoryEvent>) eventsObj;
						
						Optional<HistoryEvent> activityResult = events.stream()
							.filter(event -> {
								return ActivityTaskCompleted.toString().equals(event.getEventType())
									|| ActivityTaskFailed.toString().equals(event.getEventType());
							})
							.sorted(new Comparator<HistoryEvent>() {

								@Override
								public int compare(HistoryEvent o1, HistoryEvent o2) {
									int answer;
						            if (o1.getEventId() == o2.getEventId()) {
						                answer = 0;
						            } else if (o1.getEventId() > o2.getEventId()) {
						                answer = 1;
						            } else {
						                answer = -1;
						            }
						            return -1 * answer;
								}
								
							})
							.findFirst();
						if(activityResult.isPresent()) {
							String result = activityResult.get().getActivityTaskCompletedEventAttributes()
								.getResult();
							exchange.getOut().setBody(result);
							exchange.getOut().setHeaders(exchange.getIn().getHeaders());
						}
							
					} else if(eventsObj != null && eventsObj instanceof HistoryEvent) {
						HistoryEvent event = (HistoryEvent) eventsObj;
						if(EventType.WorkflowExecutionStarted.toString().equals(event.getEventType())) {
							String input = event.getWorkflowExecutionStartedEventAttributes().getInput();
							exchange.getOut().setBody(input);
							exchange.getOut().setHeaders(exchange.getIn().getHeaders());
						}
					} /*else if(eventsObj != null && eventsObj instanceof OrderDTO) {
						exchange.getOut().setBody(eventsObj);
						exchange.getOut().setHeaders(exchange.getIn().getHeaders());
					}*/ else {
						System.out.println("INSIDE DECIDENEXTSTEPS NO MANS LAND!!!");
					}
					
				}
			})
			.log("Received base 64 string ${body}")
			.process(new Processor() {
				
				@Override
				public void process(Exchange exchange) throws Exception {
					String base64Body = (String)exchange.getIn().getBody();
					base64Body = base64Body.replaceAll("\"", "");
					System.out.println(base64Body);
					String jsonBody =new String(Base64.getDecoder().decode(base64Body));
					exchange.getOut().setBody(jsonBody);
					exchange.getOut().setHeaders(exchange.getIn().getHeaders());
					
				}
			})
			.unmarshal()
			.json(JsonLibrary.Jackson, OrderDTO.class)
			.log("Before CHOICE == ${body}")
			.choice()
				.when(body().method("getStatus").in("Started"))
					.log("********* TO VERIFY ORDER")
					.marshal()
					.json(JsonLibrary.Jackson, OrderDTO.class)
					.setHeader(SWFConstants.EVENT_NAME, constant("VerifyOrder"))
					.setHeader(SWFConstants.VERSION, constant("3.0"))
					.to("aws-swf://activity?{{wf.route.commonOptions}}&eventName=VerifyOrder")
					.log("SENT TO VERIFY ORDER")
				.when(body().method("getState").in("VerifyOrder"))
					.log("********* TO CHARGE CREDIT CARD ")
					.marshal()
					.json(JsonLibrary.Jackson, OrderDTO.class)
					.setHeader(SWFConstants.EVENT_NAME, constant("ChargeCreditCard"))
					.setHeader(SWFConstants.VERSION, constant("3.0"))
					.to("aws-swf://activity?{{wf.route.commonOptions}}&eventName=ChargeCreditCard")
		            .log("SENT TO CHARGE CREDIT CARD")
		        .otherwise()
		        		.log("*********** WORKFLOW ENDING....")
		;
	}

}
