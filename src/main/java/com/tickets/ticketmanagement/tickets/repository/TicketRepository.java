package com.tickets.ticketmanagement.tickets.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tickets.ticketmanagement.tickets.entity.Tickets;

public interface TicketRepository extends JpaRepository<Tickets, Long>{

}
