package com.Social.application.DG2.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "OTPs")
public class OTP_ResetPassword {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String otp;

    @Column(nullable = false)
    private String mail;

    private Timestamp expirationTime;

    private Timestamp createAt;

    private boolean used;


    @PrePersist
    protected void onCreate() {
        createAt = new Timestamp(new Date().getTime());
    }
}
