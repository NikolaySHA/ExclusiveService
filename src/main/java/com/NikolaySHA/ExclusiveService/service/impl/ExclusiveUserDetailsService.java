package com.NikolaySHA.ExclusiveService.service.impl;

import com.NikolaySHA.ExclusiveService.model.entity.User;
import com.NikolaySHA.ExclusiveService.model.entity.UserRole;
import com.NikolaySHA.ExclusiveService.model.enums.UserRolesEnum;
import com.NikolaySHA.ExclusiveService.repo.UserRepository;
import com.NikolaySHA.ExclusiveService.util.ExclusiveUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class ExclusiveUserDetailsService implements UserDetailsService {
    private static final String ROLE_PREFIX = "ROLE_";
    private final UserRepository userRepository;
    
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return (UserDetails)this.userRepository.findByEmail(email).map(ExclusiveUserDetailsService::map).orElseThrow(() -> {
            return new UsernameNotFoundException("User with email " + email + " not found!");
        });
    }
    
    private static UserDetails map(User user) {
        return new ExclusiveUserDetails(user.getEmail(), user.getPassword(), user.getRoles().stream().map(UserRole::getRole).map(ExclusiveUserDetailsService::map).toList(), user.getName());
    }
    
    private static GrantedAuthority map(UserRolesEnum role) {
        return new SimpleGrantedAuthority("ROLE_" + String.valueOf(role));
    }
    
    public Authentication getAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated() ? authentication : null;
    }
    
    public ExclusiveUserDetailsService(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}
