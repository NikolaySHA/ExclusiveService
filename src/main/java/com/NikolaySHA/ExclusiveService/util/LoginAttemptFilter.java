package com.NikolaySHA.ExclusiveService.util;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.web.filter.GenericFilterBean;

public class LoginAttemptFilter extends GenericFilterBean {
    private final LoginAttemptService loginAttemptService;
    
    public LoginAttemptFilter(LoginAttemptService loginAttemptService) {
        this.loginAttemptService = loginAttemptService;
    }
    
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest)request;
        HttpServletResponse httpResponse = (HttpServletResponse)response;
        if (httpRequest.getRequestURI().equals("/users/login") && "POST".equalsIgnoreCase(httpRequest.getMethod())) {
            String email = httpRequest.getParameter("email");
            if (this.loginAttemptService.isBlocked(email)) {
                httpResponse.sendRedirect("/users/login?blocked=true");
                return;
            }
        }
        
        chain.doFilter(request, response);
    }
}
