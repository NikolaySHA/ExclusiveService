package com.NikolaySHA.ExclusiveService.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "password_reset_tokens")
@Getter
@Setter
public class PasswordResetToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String token;
    
    @OneToOne(fetch = FetchType.LAZY)  // Използваме LAZY зареждане, за да избегнем циклични зависимости при сериозни заявки
    @JoinColumn(name = "user_id", nullable = false)  // Връзка с потребителя, който иска да възстанови паролата
    private User user;
    @Column(nullable = false)
    private LocalDateTime expiryDate;
}
