package com.NikolaySHA.ExclusiveService.service;



public interface PasswordResetService {
    
    public String validatePasswordResetToken(String token);
    
    
    public boolean resetPassword(String token, String newPassword);
}