package com.kochuparet.sample.shoppingcart.orderprocessor.web;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.kochuparet.sample.shoppingcart.orderprocessor.dto.OrderDTO;

@RestController
public class WorkflowInvokeRequestController {
	
	private static Logger log = LoggerFactory.getLogger(WorkflowInvokeRequestController.class);
	
	@Produce(uri="direct:invokeWorkflow")
	private ProducerTemplate invokeWfTemplate;
	
	@RequestMapping(path="/invokeWf", method=RequestMethod.POST, produces = "application/json", consumes = "application/json")
	public Object invokeWorkflow(@RequestBody OrderDTO input) throws InterruptedException, ExecutionException {
		log.info("Received REquest === {}", input);
		input.setOrderNumber(UUID.randomUUID().toString());
		input.setStatus("Started");
		Future future = invokeWfTemplate.asyncSendBody(invokeWfTemplate.getDefaultEndpoint(), input);
		return future.get();
		
	}

}
