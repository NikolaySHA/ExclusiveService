package com.ExclusiveService.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserRegisterDTO {
    
    @Email
    @NotBlank
    private String email;
    
    @Size(min = 3 , max = 20)
    @NotBlank
    private String password;
    @Size(min = 3 , max = 20)
    @NotBlank
    private String confirmPassword;
    
    @Size(min = 3 , max = 20)
    @NotBlank
    private String name;
    @Size(min = 8 , max = 20)
    @NotBlank
    private String viberNumber;
    
    
    public UserRegisterDTO() {
    }
    
    public String getViberNumber() {
        return viberNumber;
    }
    
    public void setViberNumber(String viberNumber) {
        this.viberNumber = viberNumber;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getConfirmPassword() {
        return confirmPassword;
    }
    
    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
}
