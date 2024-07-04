package com.exclusiveService.service;

import com.exclusiveService.impl.UserDetailsServiceImpl;
import com.exclusiveService.model.dto.UserRegisterDTO;
import com.exclusiveService.model.entity.Role;
import com.exclusiveService.model.entity.User;
import com.exclusiveService.model.enums.UserRoles;
import com.exclusiveService.repo.RoleRepository;
import com.exclusiveService.repo.UserRepository;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsServiceImpl userDetailsServiceImpl;
    private final RoleRepository roleRepository;
    
    
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, UserDetailsServiceImpl userDetailsServiceImpl, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userDetailsServiceImpl = userDetailsServiceImpl;
        this.roleRepository = roleRepository;
    }
    
    public boolean register(UserRegisterDTO data){
        Optional<User> optionalUser = userRepository.findByEmail(data.getEmail());
        if (optionalUser.isPresent()){
            return false;
        }
        User user = new User();
        user.setEmail(data.getEmail());
        user.setName(data.getName());
        user.setViberNumber(data.getViberNumber());
        user.setPassword(passwordEncoder.encode(data.getPassword()));
        List<Role> roles = user.getRoles();
        if (userRepository.count() == 0){
            Role adminRole = this.roleRepository.findByName(UserRoles.ADMIN);
            roles.add(adminRole);
        }
        Role customerRole = this.roleRepository.findByName(UserRoles.CUSTOMER);
        roles.add(customerRole);
        user.setRoles(roles);
        this.userRepository.save(user);
        return true;
    }
    
    
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

    
    //    @Transactional
//    public List<Appointment> findFavourites(Long id) {
//        return customerRepository.findById(id)
//                .map(Customer::getFavouriteRecipes)
//                .orElseGet(ArrayList::new);
//        Optional<User> optionalUser = userRepository.findById(id);
//        if (optionalUser.isEmpty()) {
//            return new ArrayList<>();
//        }
//        return optionalUser.get().getFavouriteRecipes();
   //  }
   
}
