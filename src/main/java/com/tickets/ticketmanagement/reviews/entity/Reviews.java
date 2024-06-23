package com.tickets.ticketmanagement.reviews.entity;

import java.time.Instant;

import org.hibernate.annotations.ColumnDefault;

import com.tickets.ticketmanagement.events.entity.Events;
import com.tickets.ticketmanagement.users.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreRemove;
import jakarta.persistence.PreUpdate;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity
@Data
public class Reviews {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @JoinColumn(name = "event_id")
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private Events events;

    @NotNull
    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private User user;

    @NotNull
    @Size(max = 255)
    @Column(name = "rating")
    private String rating;

    @NotNull
    @Size(max = 255)
    @Column(name = "comment")
    private String comment;
    
    @NotNull
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private Instant created_at;

    @NotNull
    @Column(name = "updated_at")
    @ColumnDefault("CURRENT_TIMESTAMP")
    private Instant updated_at;

    private Instant deleted_at;

    @PrePersist
    public void prePersist() {
        this.created_at = Instant.now();
        this.updated_at = Instant.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updated_at = Instant.now();
    }

    @PreRemove
    public void preRemove() {
        this.deleted_at = Instant.now();
    }
}
