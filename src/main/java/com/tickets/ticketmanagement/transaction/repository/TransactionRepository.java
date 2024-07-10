package com.tickets.ticketmanagement.transaction.repository;

import java.time.Instant;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.tickets.ticketmanagement.transaction.entity.Transaction;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.ticketTier.event.id = :eventId AND t.createdAt > :createdAt")
    int countByEventIdAndCreatedAtAfter(Long eventId, Instant createdAt);
}
