package com.kochuparet.sample.shoppingcart.orderprocessor.service;

import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

import com.kochuparet.sample.shoppingcart.orderprocessor.dto.OrderDTO;

@Component
public class VerifyOrderProcessor {
	
	public OrderDTO process(OrderDTO request) {
		request.setOrderVerified(true);
		request.setState("VerifyOrder");
		request.setStatus("Inprogress");
		return request;
	}

}

