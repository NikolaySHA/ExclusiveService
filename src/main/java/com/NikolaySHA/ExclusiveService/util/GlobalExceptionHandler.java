package com.NikolaySHA.ExclusiveService.util;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    public GlobalExceptionHandler() {
    }
    
    @ExceptionHandler({Exception.class})
    public String handleGenericError(Model model) {
        model.addAttribute("notFoundErrorMessage", true);
        return "error-contact-admin";
    }
}