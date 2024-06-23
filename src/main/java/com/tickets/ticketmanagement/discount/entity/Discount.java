package com.tickets.ticketmanagement.discount.entity;

import java.time.Instant;
import java.util.Date;

import org.hibernate.annotations.ColumnDefault;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity
@Data
public class Discount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 255)
    @Column(name = "discount_name")
    private String discountName;

    @NotNull
    @Positive
    @Column(name = "amount", precision = 10)
    private Double amount;

    @NotNull
    @Positive
    @Column(name = "minimum_spend", precision = 10)
    private Double minimumSpend;

    @NotNull
    @Positive
    @Column(name = "usage_limit_per_user")
    private Integer usageLimitPerUser;

    @Column(name = "expiration_date")
    private Date expirationDate;

    @NotNull
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private Instant created_at;

    @NotNull
    @Column(name = "updated_at")
    @ColumnDefault("CURRENT_TIMESTAMP")
    private Instant updated_at;

    @PrePersist
    public void prePersist() {
        this.created_at = Instant.now();
        this.updated_at = Instant.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updated_at = Instant.now();
    }
}
