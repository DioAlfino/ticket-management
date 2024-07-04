package com.tickets.ticketmanagement.events.entity;

import java.time.Instant;
import java.util.List;

import org.hibernate.annotations.ColumnDefault;

import com.tickets.ticketmanagement.categories.entity.Categories;
import com.tickets.ticketmanagement.promotions.entity.Promotions;
import com.tickets.ticketmanagement.tickets.entity.Tickets;
import com.tickets.ticketmanagement.users.entity.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
@Table(name = "events")
public class Events {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Size(max = 255)
    @NotNull
    @Column(name ="event_name")
    private String name;

    @NotNull
    @Column(name = "date")
    private Instant date;

    @NotNull
    @Size(max = 255)
    @Column(name = "location")
    private String location;

    @NotNull
    @Column(columnDefinition = "TEXT", name = "description")
    private String description;

    @NotNull
    @Column(name = "is_free")
    private Boolean isFree;

    @NotNull
    @JoinColumn(name = "organizer_id")
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private User organizerId;

    @NotNull
    @JoinColumn(name = "category_id")
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private Categories categoryId;

    @Column(name = "photo_url")
    private String photoUrl;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Tickets> tickets;

    @OneToMany(mappedBy = "eventId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Promotions> promotions ;
    
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
