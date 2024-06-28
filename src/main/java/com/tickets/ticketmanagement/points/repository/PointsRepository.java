package com.tickets.ticketmanagement.points.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tickets.ticketmanagement.points.entity.Points;
import com.tickets.ticketmanagement.users.entity.User;

@Repository
public interface PointsRepository extends JpaRepository<Points, Long> {
    Optional<Points> findByUserId (User user);    
}
