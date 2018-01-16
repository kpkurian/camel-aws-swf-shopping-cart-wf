package com.kochuparet.sample.shoppingcart.orderprocessor.routes;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kochuparet.sample.shoppingcart.orderprocessor.dto.OrderDTO;
import com.kochuparet.sample.shoppingcart.orderprocessor.service.VerifyOrderProcessor;

@Component
public class VerifyOrderWfRoute extends RouteBuilder {
	
	@Autowired
	private VerifyOrderProcessor verifyOrderProcessor;

    @Override
    public void configure() throws Exception {
    		from("aws-swf://activity?{{wf.route.commonOptions}}&eventName=VerifyOrder")
    				.log("========> ACTIVITY VERIFY ORDER START")
                .setBody(simple("${body[0]}"))
                .unmarshal()
                .json(JsonLibrary.Jackson, OrderDTO.class)
                .log("RECEIVED ACTIVITY TASK VerifyOrder ${body}")
                .bean(verifyOrderProcessor)
                .log("FINISHED ACTIVITY TASK VerifyOrder ${body}")
                .marshal()
                .json(JsonLibrary.Jackson, OrderDTO.class)
                ;
    }
}
