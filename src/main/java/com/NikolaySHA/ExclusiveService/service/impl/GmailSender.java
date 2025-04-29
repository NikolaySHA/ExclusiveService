package com.NikolaySHA.ExclusiveService.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class GmailSender {
    private static final String SENDER = "exclautoservice@gmail.com";
    @Autowired
    private JavaMailSender mailSender;
    
    public GmailSender() {
    }
    
    public void sendMail(String subject, String message, String recipient) {
        SimpleMailMessage email = new SimpleMailMessage();
        email.setFrom("exclautoservice@gmail.com");
        email.setTo(recipient);
        email.setSubject(subject);
        email.setText(message);
        this.mailSender.send(email);
        System.out.println("Email sent successfully to: " + recipient);
    }
}
