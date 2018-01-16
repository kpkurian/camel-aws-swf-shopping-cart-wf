package com.kochuparet.sample.shoppingcart.orderprocessor.service;

import org.apache.camel.component.aws.swf.CamelSWFWorkflowClient;
import org.apache.camel.component.aws.swf.SWFConfiguration;
import org.apache.camel.component.aws.swf.SWFEndpoint;
import org.springframework.stereotype.Component;

//@Component
public class OrderProcessWfService {
	
	private CamelSWFWorkflowClient wfClient;

	public OrderProcessWfService(SWFEndpoint endpoint, SWFConfiguration configuration) {
		wfClient = new CamelSWFWorkflowClient(endpoint, configuration);
	}
}
