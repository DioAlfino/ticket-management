package com.tickets.ticketmanagement.points.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tickets.ticketmanagement.points.entity.Points;

@Repository
public interface PointsRepository extends JpaRepository<Points, Long> {

}
