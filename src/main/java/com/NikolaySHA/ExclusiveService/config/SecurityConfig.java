package com.NikolaySHA.ExclusiveService.config;

import com.NikolaySHA.ExclusiveService.repo.UserRepository;
import com.NikolaySHA.ExclusiveService.service.impl.ExclusiveUserDetailsService;
import com.NikolaySHA.ExclusiveService.util.LoginAttemptFilter;
import com.NikolaySHA.ExclusiveService.util.LoginAttemptService;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.RequestMatcher;

@Configuration
public class SecurityConfig {
    private final LoginAttemptService loginAttemptService;
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        return (SecurityFilterChain)httpSecurity.authorizeHttpRequests((authorizeRequests) -> {
            ((AuthorizeHttpRequestsConfigurer.AuthorizedUrl)((AuthorizeHttpRequestsConfigurer.AuthorizedUrl)((AuthorizeHttpRequestsConfigurer.AuthorizedUrl)((AuthorizeHttpRequestsConfigurer.AuthorizedUrl)authorizeRequests.requestMatchers(new RequestMatcher[]{PathRequest.toStaticResources().atCommonLocations()})).permitAll().requestMatchers(new String[]{"/", "/users/login", "/users/register", "/gallery", "/users/login-error", "/contacts", "/services", "/about", "/insurance", "/uploads/**", "/users/forgot-password", "/users/reset-password", "/error-contact-admin"})).permitAll().requestMatchers(new String[]{"/protocols", "/garage/cars", "/garage/appointments", "/garage/users", "/gallery/upload"})).hasRole("ADMIN").anyRequest()).authenticated();
        }).formLogin((formLogin) -> {
            ((FormLoginConfigurer)((FormLoginConfigurer)formLogin.loginPage("/users/login").usernameParameter("email").passwordParameter("password").defaultSuccessUrl("/home", true)).failureHandler((request, response, exception) -> {
                String email = request.getParameter("email");
                if (this.loginAttemptService.isBlocked(email)) {
                    response.sendRedirect("/users/login-error?blocked=true");
                } else {
                    this.loginAttemptService.loginFailed(email);
                    response.sendRedirect("/users/login-error");
                }
            })).successHandler((request, response, authentication) -> {
                String email = request.getParameter("email");
                this.loginAttemptService.loginSucceeded(email);
                response.sendRedirect("/home");
            });
        }).logout((logout) -> {
            logout.logoutUrl("/logout").logoutSuccessUrl("/").invalidateHttpSession(true).deleteCookies(new String[]{"JSESSIONID"}).permitAll();
        }).csrf((csrf) -> {
            csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
        }).addFilterBefore(new LoginAttemptFilter(this.loginAttemptService), UsernamePasswordAuthenticationFilter.class).exceptionHandling((exceptionHandling) -> {
            exceptionHandling.authenticationEntryPoint((request, response, authException) -> {
                String requestURI = request.getRequestURI();
                if (requestURI.contains("/appointments/add")) {
                    request.getSession().setAttribute("showRegisteredErrorMessage", true);
                    response.sendRedirect("/users/login");
                } else {
                    response.sendRedirect("/error-contact-admin");
                }
                
            }).accessDeniedPage("/error-contact-admin");
        }).build();
    }
    
    @Bean
    public ExclusiveUserDetailsService userDetailsService(UserRepository userRepository) {
        return new ExclusiveUserDetailsService(userRepository);
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return Pbkdf2PasswordEncoder.defaultsForSpringSecurity_v5_8();
    }
    
    @Bean
    public AuthenticationManager authenticationManager(ExclusiveUserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(new AuthenticationProvider[]{provider});
    }
    
    public SecurityConfig(final LoginAttemptService loginAttemptService) {
        this.loginAttemptService = loginAttemptService;
    }
}
