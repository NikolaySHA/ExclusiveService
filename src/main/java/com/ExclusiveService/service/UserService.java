package com.ExclusiveService.service;

import com.ExclusiveService.util.UserDetailsService;
import com.ExclusiveService.model.dto.RegisterDTO;
import com.ExclusiveService.model.entity.Role;
import com.ExclusiveService.model.entity.User;
import com.ExclusiveService.model.enums.UserRoles;
import com.ExclusiveService.repo.RoleRepository;
import com.ExclusiveService.repo.UserRepository;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public interface UserService {
   
    
    boolean register(RegisterDTO data);
    
    
    User findLoggedUser();

    
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
