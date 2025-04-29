package com.NikolaySHA.ExclusiveService.config;

import jakarta.servlet.Filter;
import jakarta.servlet.ServletContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;
import org.springframework.web.multipart.support.MultipartFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

@Configuration
public class WebConfig extends AbstractSecurityWebApplicationInitializer implements WebMvcConfigurer {
    private final LocaleChangeInterceptor localeChangeInterceptor;
    
    public WebConfig(LocaleChangeInterceptor localeChangeInterceptor) {
        this.localeChangeInterceptor = localeChangeInterceptor;
    }
    
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(new String[]{"/uploads/**"}).addResourceLocations(new String[]{"file:uploads/"});
    }
    
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(this.localeChangeInterceptor);
    }
    
    protected void beforeSpringSecurityFilterChain(ServletContext servletContext) {
        this.insertFilters(servletContext, new Filter[]{new MultipartFilter()});
    }
}
