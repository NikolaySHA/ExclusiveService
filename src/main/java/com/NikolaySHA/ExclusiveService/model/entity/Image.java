package com.NikolaySHA.ExclusiveService.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(
        name = "Images"
)
@Getter
@Setter
@NoArgsConstructor
public class Image {
    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long id;
    @Column(
            nullable = false
    )
    private String title;
    @Column(
            nullable = false
    )
    private String url;
    @Column(
            nullable = false
    )
    private String contentType;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(
            nullable = false,
            updatable = false
    )
    private Date uploadedAt = new Date();
    
}
