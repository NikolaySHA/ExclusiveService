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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    boolean register(UserRegisterDTO data);
    
    User findLoggedUser();
    
    boolean loggedUserHasRole(String role);
    
    Page<User> findAllUsersWithRoles(Pageable pageable);
    
    Page<User> searchUsers(String name, String licensePlate, String email, UserRolesEnum role, Pageable pageable);
    
    Optional<User> findById(Long id);
    
    boolean updateUser(Long id, UserEditDTO user);
    
    void addAdmin(Long userId);
    
    boolean removeAdmin(Long userId);
    
    boolean isAdmin(List<UserRole> roles);
    
    Page<User> findAll(Pageable pageable);
    
    default boolean sendPasswordResetLink(String email) throws MessagingException, GeneralSecurityException, IOException {
        return false;
    }
}
