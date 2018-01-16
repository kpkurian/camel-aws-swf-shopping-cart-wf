package com.kochuparet.sample.shoppingcart.orderprocessor;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AwsOrderProcessingSwfApplication {
	
	public static ConcurrentHashMap<String, String> orderIdRunIdMapping = new ConcurrentHashMap<>();

	public static void main(String[] args) {
		SpringApplication.run(AwsOrderProcessingSwfApplication.class, args);
	}
}
