package com.tickets.ticketmanagement.events.entity;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

import org.hibernate.annotations.ColumnDefault;

import com.tickets.ticketmanagement.categories.entity.Categories;
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
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "events")
public class Events {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Size(max = 255)
    @NotNull
    @Column(name ="name")
    private String name;

    @NotNull
    @Column(columnDefinition = "TEXT", name = "description")
    private String description;

    @NotNull
    @Size(max = 255)
    @Column(name = "location")
    private String location;

    @NotNull
    @Column(name = "date")
    private LocalDate date;

    @NotNull
    @Column(name = "time")
    private LocalTime time;

    @NotNull
    @Positive
    @Column(name = "price", precision = 10)
    private Double price;

    @NotNull
    @Positive
    @Column(name = "available_seats")
    private Integer availableSeats;

    @Size(max = 50)
    @NotNull
    @Column(name = "event_type")
    private String eventType;

    @NotNull
    @JoinColumn(name = "organizer_id")
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private User organizerId;

    @NotNull
    @JoinColumn(name = "category_id")
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private Categories categoriesId;
    
    @NotNull
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @NotNull
    @Column(name = "updated_at")
    @ColumnDefault("CURRENT_TIMESTAMP")
    private Instant updatedAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @PrePersist
    public void prePersist() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = Instant.now();
    }

    @PreRemove
    public void preRemove() {
        this.deletedAt = Instant.now();
    }
}
