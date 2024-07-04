package com.ExclusiveService.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class CustomerUserDetails implements UserDetails {
    
    private final String username;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;
    private final String name;
    
    public CustomerUserDetails(String email, String password, Collection<? extends GrantedAuthority> authorities, String name) {
        this.username = email;
        this.password = password;
        this.authorities = authorities;
        this.name = name;
    }
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }
    
    @Override
    public String getPassword() {
        return password;
    }
    
    @Override
    public String getUsername() {
        return username;
    }
    
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    
    @Override
    public boolean isEnabled() {
        return true;
    }
    
    public String getName() {
        return name;
    }
}
