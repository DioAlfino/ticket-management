package com.tickets.ticketmanagement.reviews.entity;

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
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity
@Data
@Table(name = "review")
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
    @Column(name = "rating")
    private Integer rating;

    @NotNull
    @Size(max = 255)
    @Column(name = "comment")
    private String comment;

}
