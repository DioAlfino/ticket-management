package com.tickets.ticketmanagement.transaction.entity;

import java.time.Instant;

import com.tickets.ticketmanagement.points.entity.Points;
import com.tickets.ticketmanagement.referrals.entity.Referrals;
import com.tickets.ticketmanagement.tickets.entity.Tickets;
import com.tickets.ticketmanagement.users.entity.User;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ticket_tier_id")
    private Tickets ticketTier;

    @ManyToOne
    @JoinColumn(name = "participant_id")
    private User participant;

    @ManyToOne
    @JoinColumn(name = "referral_id")
    private Referrals referral;

    @ManyToOne
    @JoinColumn(name = "point_id")
    private Points points;

    private double finalAmount;
    private Instant createdAt;
}
