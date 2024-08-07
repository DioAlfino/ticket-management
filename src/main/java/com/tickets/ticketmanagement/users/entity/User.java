package com.tickets.ticketmanagement.users.entity;

import java.security.SecureRandom;
import java.time.Instant;


import org.hibernate.annotations.ColumnDefault;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreRemove;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Size(max = 255)
    @NotNull
    @Column(name ="name")
    private String name;

    @Size(max = 255)
    @NotNull
    @Column(name = "email")
    private String email;

    @JsonIgnore
    @NotNull
    @Column(name = "password")
    private String password;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "role_name")
    private RolesType role;

    @NotNull
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private Instant created_at;

    @NotNull
    @Column(name = "updated_at")
    @ColumnDefault("CURRENT_TIMESTAMP")
    private Instant updated_at;

    private Instant deleted_at;

    @Size(max = 10)
    @Column(name = "referral_code", unique = true)
    private String referralCode;

    @PrePersist
    public void prePersist() {
        this.created_at = Instant.now();
        this.updated_at = Instant.now();
        this.referralCode = generateReferralCode();
    }

    @PreUpdate
    public void preUpdate() {
        this.updated_at = Instant.now();
    }

    @PreRemove
    public void preRemove() {
        this.deleted_at = Instant.now();
    }

    private String generateReferralCode() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder referralCode = new StringBuilder();

        for (int i = 0; i < 8; i++) {
            referralCode.append(characters.charAt(random.nextInt(characters.length())));
        }
        return referralCode.toString();
    }
}
