package com.NikolaySHA.ExclusiveService.service.impl;

import com.NikolaySHA.ExclusiveService.model.entity.PasswordResetToken;
import com.NikolaySHA.ExclusiveService.model.entity.User;
import com.NikolaySHA.ExclusiveService.repo.PasswordResetTokenRepository;
import com.NikolaySHA.ExclusiveService.repo.UserRepository;
import com.NikolaySHA.ExclusiveService.service.PasswordResetService;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
    
    public String validatePasswordResetToken(String token) {
        Optional<PasswordResetToken> tokenOptional = this.tokenRepository.findByToken(token);
        if (tokenOptional.isEmpty()) {
            return "Invalid token";
        } else {
            PasswordResetToken resetToken = (PasswordResetToken)tokenOptional.get();
            return resetToken.getExpiryDate().isBefore(LocalDateTime.now()) ? "Token expired" : null;
        }
    }
    
    public boolean resetPassword(String token, String newPassword) {
        Optional<PasswordResetToken> tokenOptional = this.tokenRepository.findByToken(token);
        if (tokenOptional.isEmpty()) {
            return false;
        } else {
            PasswordResetToken resetToken = (PasswordResetToken)tokenOptional.get();
            User user = resetToken.getUser();
            user.setPassword(this.passwordEncoder.encode(newPassword));
            this.userRepository.save(user);
            this.tokenRepository.delete(resetToken);
            return true;
        }
    }
}
