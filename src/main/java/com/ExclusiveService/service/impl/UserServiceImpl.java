package com.ExclusiveService.service.impl;

import com.ExclusiveService.model.dto.RegisterDTO;
import com.ExclusiveService.model.entity.Role;
import com.ExclusiveService.model.entity.User;
import com.ExclusiveService.model.enums.UserRoles;
import com.ExclusiveService.repo.RoleRepository;
import com.ExclusiveService.repo.UserRepository;
import com.ExclusiveService.service.UserService;
import com.ExclusiveService.util.UserDetailsService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;
    private final RoleRepository roleRepository;
    
    
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, UserDetailsService userDetailsService, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userDetailsService = userDetailsService;
        this.roleRepository = roleRepository;
    }
    
    @Override
    public boolean register(RegisterDTO data) {
        Optional<User> optionalUser = userRepository.findByEmail(data.getEmail());
        if (optionalUser.isPresent()){
            return false;
        }
        User user = new User();
        user.setEmail(data.getEmail());
        user.setName(data.getName());
        user.setViberNumber(data.getViberNumber());
        user.setPassword(passwordEncoder.encode(data.getPassword()));
        
        List<Role> roles = new ArrayList<>();
        if (userRepository.count() == 0){
            Role adminRole = roleRepository.findByName(UserRoles.ADMIN);
            roles.add(adminRole);
        }
        Role customerRole = roleRepository.findByName(UserRoles.CUSTOMER);
        roles.add(customerRole);
        user.setRoles(roles);
        
        userRepository.save(user);
        return true;
    }
    
    @Override
    public User findLoggedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails) {
                Optional<User> user = userRepository.findByEmail(((UserDetails) principal).getUsername());
                if (user.isPresent()){
                    return user.get();
                }
            }
        }
        return null;
    }
}
