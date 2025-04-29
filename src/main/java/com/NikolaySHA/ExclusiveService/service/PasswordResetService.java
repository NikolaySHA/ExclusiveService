package com.NikolaySHA.ExclusiveService.service;

public interface PasswordResetService {
    String validatePasswordResetToken(String token);
    
    boolean resetPassword(String token, String newPassword);
}