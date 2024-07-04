package com.tickets.ticketmanagement.promotions.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tickets.ticketmanagement.promotions.entity.Promotions;

public interface PromotionsRepository extends JpaRepository<Promotions, Long> {

}
