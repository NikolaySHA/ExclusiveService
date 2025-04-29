package com.NikolaySHA.ExclusiveService.service.impl;

import com.NikolaySHA.ExclusiveService.model.dto.userDTO.UserEditDTO;
import com.NikolaySHA.ExclusiveService.model.dto.userDTO.UserRegisterDTO;
import com.NikolaySHA.ExclusiveService.model.entity.PasswordResetToken;
import com.NikolaySHA.ExclusiveService.model.entity.User;
import com.NikolaySHA.ExclusiveService.model.entity.UserRole;
import com.NikolaySHA.ExclusiveService.model.enums.UserRolesEnum;
import com.NikolaySHA.ExclusiveService.repo.PasswordResetTokenRepository;
import com.NikolaySHA.ExclusiveService.repo.RoleRepository;
import com.NikolaySHA.ExclusiveService.repo.UserRepository;
import com.NikolaySHA.ExclusiveService.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ExclusiveUserDetailsService exclusiveUserDetailsService;
    private final RoleRepository roleRepository;
    private final ModelMapper modelMapper;
    private final GmailSender emailSender;
    private final PasswordResetTokenRepository tokenRepository;
    
    public boolean register(UserRegisterDTO data) {
        Optional<User> optionalUser = this.userRepository.findByEmail(data.getEmail());
        if (optionalUser.isPresent()) {
            return false;
        } else {
            User user = (User)this.modelMapper.map(data, User.class);
            user.setPassword(this.passwordEncoder.encode(data.getPassword()));
            List<UserRole> userRoles = new ArrayList();
            UserRole customerUserRole;
            if (this.userRepository.count() == 0L) {
                customerUserRole = this.roleRepository.findByRole(UserRolesEnum.ADMIN);
                userRoles.add(customerUserRole);
            }
            
            customerUserRole = this.roleRepository.findByRole(UserRolesEnum.CUSTOMER);
            userRoles.add(customerUserRole);
            user.setRoles(userRoles);
            this.userRepository.save(user);
            return true;
        }
    }
    
    @Transactional
    public User findLoggedUser() {
        String email = this.exclusiveUserDetailsService.getAuthentication().getName();
        return this.userRepository.findByEmail(email).orElse( null);
    }
    
    @Transactional
    public boolean loggedUserHasRole(String role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            Object var4 = authentication.getPrincipal();
            if (var4 instanceof UserDetails) {
                UserDetails userDetails = (UserDetails)var4;
                return userDetails.getAuthorities().stream().anyMatch((grantedAuthority) -> {
                    return grantedAuthority.getAuthority().equals("ROLE_" + role);
                });
            }
        }
        
        return false;
    }
    
    public Page<User> findAllUsersWithRoles(Pageable pageable) {
        return this.userRepository.findAllWithRoles(pageable);
    }
    
    public Page<User> searchUsers(String name, String licensePlate, String email, UserRolesEnum role, Pageable pageable) {
        return this.userRepository.searchUsers(name, licensePlate, email, role, pageable);
    }
    
    public Optional<User> findById(Long id) {
        return this.userRepository.findById(id);
    }
    
    public boolean updateUser(Long id, UserEditDTO updatedUser) {
        Optional<User> toEdit = this.userRepository.findById(id);
        if (toEdit.isEmpty()) {
            return false;
        } else {
            User user = (User)toEdit.get();
            user.setName(updatedUser.getName());
            String email = updatedUser.getEmail();
            if (!user.getEmail().equals(email) && this.userRepository.findByEmail(email).isPresent()) {
                return false;
            } else {
                user.setEmail(email);
                user.setPhoneNumber(updatedUser.getPhoneNumber());
                this.userRepository.save(user);
                return true;
            }
        }
    }
    
    public void addAdmin(Long userId) {
        User user = (User)this.userRepository.findById(userId).get();
        List<UserRole> roles = user.getRoles();
        UserRole role = this.roleRepository.findByRole(UserRolesEnum.ADMIN);
        if (!roles.contains(role)) {
            roles.add(role);
            user.setRoles(roles);
            this.userRepository.save(user);
        }
    }
    
    public boolean removeAdmin(Long userId) {
        User user = (User)this.userRepository.findById(userId).get();
        List<UserRole> roles = user.getRoles();
        UserRole role = this.roleRepository.findByRole(UserRolesEnum.ADMIN);
        if (!roles.contains(role)) {
            return false;
        } else {
            long count = this.userRepository.findAll().stream().filter((u) -> {
                return u.getRoles().stream().anyMatch((role1) -> {
                    return role1.getRole().name().equals("ADMIN");
                });
            }).count();
            if (count < 2L) {
                return false;
            } else {
                roles.remove(role);
                user.setRoles(roles);
                this.userRepository.save(user);
                return true;
            }
        }
    }
    
    public boolean isAdmin(List<UserRole> roles) {
        return roles.stream().anyMatch((role) -> {
            return role.getRole().name().equals("ADMIN");
        });
    }
    
    public Page<User> findAll(Pageable pageable) {
        return this.userRepository.findAll(pageable);
    }
    
    public boolean sendPasswordResetLink(String email) throws MessagingException, GeneralSecurityException, IOException {
        Optional<User> userOptional = this.userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            User user = (User)userOptional.get();
            String resetToken = UUID.randomUUID().toString();
            PasswordResetToken token = new PasswordResetToken();
            token.setUser(user);
            token.setToken(resetToken);
            token.setExpiryDate(LocalDateTime.now().plusHours(1L));
            this.tokenRepository.save(token);
            String resetLink = "http://exclusiveservice.bg:8080/users/reset-password?token=" + resetToken;
            this.emailSender.sendMail("Възстановяване на парола", "Моля натиснете линка: \n" + resetLink + "\nза да възстановите паролата си.", email);
            return true;
        } else {
            return false;
        }
    }
    
    public UserServiceImpl(final UserRepository userRepository, final PasswordEncoder passwordEncoder, final ExclusiveUserDetailsService exclusiveUserDetailsService, final RoleRepository roleRepository, final ModelMapper modelMapper, final GmailSender emailSender, final PasswordResetTokenRepository tokenRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.exclusiveUserDetailsService = exclusiveUserDetailsService;
        this.roleRepository = roleRepository;
        this.modelMapper = modelMapper;
        this.emailSender = emailSender;
        this.tokenRepository = tokenRepository;
    }
}
