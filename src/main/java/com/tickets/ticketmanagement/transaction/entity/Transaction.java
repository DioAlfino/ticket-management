package com.tickets.ticketmanagement.transaction.entity;

import java.time.Instant;

import com.tickets.ticketmanagement.users.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "transaction")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "participant_id")
    private User participant;

    @Column(name = "total_amount")
    private double totalAmount;
    private double discount;

    @Column(name = "points_used")
    private double pointsUsed;

    @Column(name = "final_amount")
    private double finalAmount;

    @Column(name = "created_at")
    private Instant createdAt;
}
