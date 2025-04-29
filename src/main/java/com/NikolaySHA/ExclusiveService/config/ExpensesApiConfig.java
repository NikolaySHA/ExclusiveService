package com.NikolaySHA.ExclusiveService.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(
        prefix = "expenses.api"
)
public class ExpensesApiConfig {
    private String baseUrl;
    
    public ExpensesApiConfig() {
    }
    
    public String getBaseUrl() {
        return this.baseUrl;
    }
    
    public void setBaseUrl(final String baseUrl) {
        this.baseUrl = baseUrl;
    }
}