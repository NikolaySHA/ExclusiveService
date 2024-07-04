package com.ExclusiveService.controller;

import com.ExclusiveService.model.dto.UserLoginDTO;
import com.ExclusiveService.model.dto.UserRegisterDTO;
import com.ExclusiveService.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class UserController {
    
    private final UserService userService;
  
    
    public UserController(UserService userService) {
        this.userService = userService;
        
    }
    
    @ModelAttribute("registerData")
    public UserRegisterDTO customerRegisterDTO(){
        return new UserRegisterDTO();
    }
    @ModelAttribute("loginData")
    public UserLoginDTO customerLoginDTO(){
        return new UserLoginDTO();
    }
    
    
    
    @GetMapping("/users/register")
    public String register(){
       
        return "register";
    }
    
    @PostMapping("/users/register")
    public String doRegister(@Valid UserRegisterDTO data){
        
        if (!data.getPassword().equals(data.getConfirmPassword())){
            return "register";
        }
        boolean success = userService.register(data);
        if (!success) {
            return "redirect:/users/register";
        }
        return "redirect:/users/login";
    }
    @GetMapping("/users/login")
    public ModelAndView viewLogin() {
        ModelAndView modelAndView = new ModelAndView("login");
        modelAndView.addObject("loginData", new UserLoginDTO());
        return modelAndView;
    }
    @GetMapping("/users/login-error")
    public ModelAndView viewLoginError() {
        ModelAndView modelAndView = new ModelAndView("login");
        
        modelAndView.addObject("loginData", new UserLoginDTO());
        modelAndView.addObject("showErrorMessage", true);
        return modelAndView;
    }

    
}
