package com.NikolaySHA.ExclusiveService.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestConfig {
    public RestConfig() {
    }
    
    @Bean({"expensesRestClient"})
    public RestClient expensesRestClient(ExpensesApiConfig expensesApiConfig) {
        return RestClient.builder().baseUrl(expensesApiConfig.getBaseUrl()).defaultHeader("Content-Type", new String[]{"application/json"}).build();
    }
}