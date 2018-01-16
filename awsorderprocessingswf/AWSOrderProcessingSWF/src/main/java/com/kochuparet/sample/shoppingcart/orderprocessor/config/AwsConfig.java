package com.kochuparet.sample.shoppingcart.orderprocessor.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSCredentialsProviderChain;
import com.amazonaws.auth.ContainerCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.auth.EC2ContainerCredentialsProviderWrapper;
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.auth.SystemPropertiesCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.simpleworkflow.AmazonSimpleWorkflowClient;
import com.amazonaws.services.simpleworkflow.AmazonSimpleWorkflowClientBuilder;

@Configuration
public class AwsConfig {
	
	@Bean
    public AWSCredentialsProvider awsCredentialProvider() {
        //System.out.println("Loading AWS Credential Provider");
        List<AWSCredentialsProvider> credentialProviders = new ArrayList<>();
        credentialProviders.add(new EC2ContainerCredentialsProviderWrapper());
        credentialProviders.add(new ContainerCredentialsProvider());
        credentialProviders.add(new EnvironmentVariableCredentialsProvider());
        credentialProviders.add(new SystemPropertiesCredentialsProvider());
        credentialProviders.add(new ProfileCredentialsProvider());
        credentialProviders.add(new DefaultAWSCredentialsProviderChain());
        AWSCredentialsProviderChain chain = new AWSCredentialsProviderChain(credentialProviders);
        chain.setReuseLastProvider(true);
        return chain;
    }
	
	@Bean("swfClient")
	public AmazonSimpleWorkflowClient getAwsSwfClient(AWSCredentialsProvider awsCredentialsProvider) {
		AmazonSimpleWorkflowClient client = (AmazonSimpleWorkflowClient) AmazonSimpleWorkflowClientBuilder
												.standard()
												.withRegion("us-east-1")
												.withCredentials(awsCredentialsProvider)
												.build();
		return client;
	}

}

