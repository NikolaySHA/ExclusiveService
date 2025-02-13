package com.NikolaySHA.ExclusiveService.config;

import com.NikolaySHA.ExclusiveService.repo.UserRepository;
import com.NikolaySHA.ExclusiveService.service.impl.ExclusiveUserDetailsService;
import com.NikolaySHA.ExclusiveService.util.LoginAttemptFilter;
import com.NikolaySHA.ExclusiveService.util.LoginAttemptService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    
    private final LoginAttemptService loginAttemptService;
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                                .requestMatchers("/", "/users/login", "/users/register", "/gallery",
                                        "/users/login-error", "/contacts", "/services", "/about",
                                        "/insurance", "/uploads/**", "/users/forgot-password", "/users/reset-password").permitAll()
                                .requestMatchers("/protocols", "/garage/cars", "/garage/appointments",
                                        "/garage/users", "/gallery/upload").hasRole("ADMIN")
                                .anyRequest().authenticated()
                )
                .formLogin(formLogin ->
                        formLogin
                                .loginPage("/users/login")
                                .usernameParameter("email")
                                .passwordParameter("password")
                                .defaultSuccessUrl("/home", true)
                                .failureHandler((request, response, exception) -> {
                                    String email = request.getParameter("email");
                                    
                                    if (loginAttemptService.isBlocked(email)) {
                                        response.sendRedirect("/users/login-error?blocked=true");
                                        return;
                                    }
                                    
                                    loginAttemptService.loginFailed(email);
                                    response.sendRedirect("/users/login-error");
                                })
                                .successHandler((request, response, authentication) -> {
                                    String email = request.getParameter("email");
                                    loginAttemptService.loginSucceeded(email);
                                    response.sendRedirect("/home");
                                })
                )
                .logout(logout ->
                        logout
                                .logoutUrl("/logout")
                                .logoutSuccessUrl("/")
                                .invalidateHttpSession(true)
                                .deleteCookies("JSESSIONID")
                                .permitAll()
                ).csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                )
                .addFilterBefore(new LoginAttemptFilter(loginAttemptService), UsernamePasswordAuthenticationFilter.class)
                .build();
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
        return new ProviderManager(provider);
    }
}
