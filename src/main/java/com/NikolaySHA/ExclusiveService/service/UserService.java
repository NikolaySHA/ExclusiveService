package com.NikolaySHA.ExclusiveService.service;

import com.NikolaySHA.ExclusiveService.model.dto.userDTO.UserEditDTO;
import com.NikolaySHA.ExclusiveService.model.dto.userDTO.UserRegisterDTO;
import com.NikolaySHA.ExclusiveService.model.entity.User;
import com.NikolaySHA.ExclusiveService.model.entity.UserRole;
import com.NikolaySHA.ExclusiveService.model.enums.UserRolesEnum;
import jakarta.mail.MessagingException;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Optional;


public interface UserService {
   
    
    boolean register(UserRegisterDTO data);
    
    User findLoggedUser();
    
    boolean loggedUserHasRole(String role);
    
    
    List<User> findAllUsersWithRoles();
    
    List<User> searchUsers(String name, String email, UserRolesEnum role);
    
    Optional<User> findById(Long id);
    boolean updateUser(Long id, UserEditDTO user);
    
    void addAdmin(Long userId);
    
    boolean removeAdmin(Long userId);
    
    boolean isAdmin(List<UserRole> roles);
    
    List<User> findAll();
    
    public default boolean sendPasswordResetLink(String email) throws MessagingException, GeneralSecurityException, IOException {
        return false;
    }
}
