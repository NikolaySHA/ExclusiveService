package com.NikolaySHA.ExclusiveService.web.controller;

import com.NikolaySHA.ExclusiveService.service.impl.GmailSender;
import jakarta.mail.MessagingException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping({"/contacts"})
public class ContactController {
    private final GmailSender emailSender;
    
    public ContactController(GmailSender emailSender) {
        this.emailSender = emailSender;
    }
    
    @GetMapping
    public String contacts() {
        return "home-contacts";
    }
    
    @PostMapping
    public String sendEmail(@RequestParam String name, @RequestParam String email, @RequestParam(required = false) String phone, @RequestParam String subject, @RequestParam String message, RedirectAttributes redirectAttributes) throws MessagingException, GeneralSecurityException, IOException {
        String to = "exclautoservice@gmail.com";
        String emailSubject = "Ново запитване: " + subject;
        String emailText = String.format("Име: %s\nEmail: %s\nТелефон: %s\nТема: %s\nСъобщение: %s", name, email, phone, subject, message);
        redirectAttributes.addFlashAttribute("successContactMessage", true);
        this.emailSender.sendMail(emailSubject, emailText, "exclautoservice@gmail.com");
        return "redirect:/contacts";
    }
}
