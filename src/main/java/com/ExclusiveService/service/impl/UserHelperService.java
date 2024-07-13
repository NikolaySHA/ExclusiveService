package com.ExclusiveService.service.impl;

import com.ExclusiveService.model.entity.User;
import com.ExclusiveService.repo.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;


public class UserHelperService {
    private static final String ROLE_PREFIX = "ROLE_";
    private final UserRepository userRepository;
    
    public UserHelperService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
   public boolean hasRole(String role){
        return getUserDetails()
                .getAuthorities()
                .stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(ROLE_PREFIX + role));
   }
   public User getUser(){
        return userRepository.findByEmail(getUserDetails().getUsername())
                .orElse(null);
   }

    public UserDetails getUserDetails() {
        return (UserDetails) getAuthentication().getPrincipal();
    }
    public boolean isAuthenticated(){
        return !hasRole("ANONYMOUS");
        
    }
    public Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
    
}
