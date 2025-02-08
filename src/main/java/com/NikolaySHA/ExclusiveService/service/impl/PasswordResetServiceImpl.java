package com.NikolaySHA.ExclusiveService.service.impl;

import com.NikolaySHA.ExclusiveService.model.entity.PasswordResetToken;
import com.NikolaySHA.ExclusiveService.model.entity.User;
import com.NikolaySHA.ExclusiveService.repo.PasswordResetTokenRepository;
import com.NikolaySHA.ExclusiveService.repo.UserRepository;
import com.NikolaySHA.ExclusiveService.service.PasswordResetService;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class PasswordResetServiceImpl implements PasswordResetService {
    private final UserRepository userRepository;
    
    private final PasswordResetTokenRepository tokenRepository;
    
    private final PasswordEncoder passwordEncoder;
    
    public PasswordResetServiceImpl(UserRepository userRepository, PasswordResetTokenRepository tokenRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    @Override
    public String validatePasswordResetToken(String token) {
        Optional<PasswordResetToken> tokenOptional = tokenRepository.findByToken(token);
        if (tokenOptional.isEmpty()) {
            return "Invalid token";
        }
        PasswordResetToken resetToken = tokenOptional.get();
        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            return "Token expired";
        }
        return null;
    }
    
    @Override
    public boolean resetPassword(String token, String newPassword) {
        Optional<PasswordResetToken> tokenOptional = tokenRepository.findByToken(token);
        if (tokenOptional.isEmpty()) {
            return false;
        }
        
        PasswordResetToken resetToken = tokenOptional.get();
        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        
        // Изтриваме използвания токен
        tokenRepository.delete(resetToken);
        return true;
    }
}
