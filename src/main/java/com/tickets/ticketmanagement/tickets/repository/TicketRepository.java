package com.tickets.ticketmanagement.tickets.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tickets.ticketmanagement.tickets.entity.Tickets;

@Repository
public interface TicketRepository extends JpaRepository<Tickets, Long> {

    List<Tickets> findByEventId(Long id);

    @Query("SELECT t FROM Tickets t WHERE t.event.id=:eventId")
    List<Tickets> allTicketTier(@Param("eventId")Long eventId);


}
