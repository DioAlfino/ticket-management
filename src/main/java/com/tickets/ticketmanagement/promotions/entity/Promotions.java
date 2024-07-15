package com.tickets.ticketmanagement.promotions.entity;

import java.time.Duration;
import java.time.Instant;

import org.hibernate.annotations.ColumnDefault;

import com.tickets.ticketmanagement.events.entity.Events;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity
@Data
@Table(name = "promotion_event")
public class Promotions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(max = 255)
    @Column(name = "name")
    private String name;

    @NotNull
    @JoinColumn(name = "event_id")
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private Events eventId;

    @Column(name = "discount", precision = 10, nullable = false)
    private Double discount;

    @Column(name = "max_user", nullable = false)
    private Integer maxUser;

    @NotNull
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "start_date", updatable = false)
    private Instant startDate;

    @NotNull
    @Column(name = "end_date")
    @ColumnDefault("CURRENT_TIMESTAMP")
    private Instant endDate;

    @PrePersist
    public void prePersist() {
        Instant now = Instant.now();
        this.startDate = now;
        this.endDate = now.plus(Duration.ofDays(7));
    }

    @PreUpdate
    public void preUpdate() {
        if (this.endDate == null || this.endDate.isBefore(Instant.now())) {
            this.endDate = Instant.now().plus(Duration.ofDays(7));
        }
    }

}
