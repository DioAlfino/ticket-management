package com.tickets.ticketmanagement.promotions.repository;

import java.time.Instant;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tickets.ticketmanagement.promotions.entity.Promotions;

public interface PromotionsRepository extends JpaRepository<Promotions, Long> {
    @Query("SELECT p FROM Promotions p WHERE p.startDate <= :now AND p.endDate >= :now AND p.maxUser > 0 AND p.eventId.id = :eventId")
    Promotions findActivePromotionForEvent(@Param("now") Instant now, @Param("eventId") Long eventId);
}
