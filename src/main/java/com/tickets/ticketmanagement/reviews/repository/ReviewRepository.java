package com.tickets.ticketmanagement.reviews.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tickets.ticketmanagement.reviews.entity.Reviews;

@Repository
public interface ReviewRepository extends JpaRepository<Reviews, Long> {

    boolean existsByEvents_IdAndUser_Email(Long eventId, String userEmail);
}