package com.tickets.ticketmanagement.referrals.entity;



import java.time.Instant;
import java.time.temporal.ChronoUnit;

import com.tickets.ticketmanagement.users.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "referral")
public class Referrals {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "referring_user_id", nullable = false)
    private User referringUser;

    @Column(name = "discountAmount", nullable = false)
    private double discountAmount;

    @Column(name = "is_used")
    private boolean isUsed;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
    
    @Column(name = "expired_at", nullable = false)
    private Instant expiredAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        expiredAt = createdAt.plus(90, ChronoUnit.DAYS);
    }

}
