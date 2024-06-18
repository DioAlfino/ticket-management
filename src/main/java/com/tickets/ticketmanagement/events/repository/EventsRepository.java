package com.tickets.ticketmanagement.events.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tickets.ticketmanagement.events.entity.Events;

@Repository
public interface EventsRepository extends JpaRepository<Events, Long> {
Optional<Events> findByName(String name);
}
