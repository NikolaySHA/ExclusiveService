package com.ExclusiveService.util;

import com.ExclusiveService.util.UserDetails;
import com.ExclusiveService.model.entity.User;
import com.ExclusiveService.repo.UserRepository;
import org.hibernate.Hibernate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {
    private final UserRepository userRepository;
    
    public UserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User with email: " + email + " not found!"));
        
        Hibernate.initialize(user.getRoles());
        List<GrantedAuthority> authorities = mapToGrantedAuthority(user);
        
        return new UserDetails(
                user.getEmail(),
                user.getPassword(),
                authorities,
                user.getName()
        );
    }
    
    private List<GrantedAuthority> mapToGrantedAuthority(User user) {
        return user.getRoles()
                .stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName().name()))
                .collect(Collectors.toUnmodifiableList());
    }
}
