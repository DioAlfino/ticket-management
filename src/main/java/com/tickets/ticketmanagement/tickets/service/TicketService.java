package com.tickets.ticketmanagement.tickets.service;

import java.util.List;

import com.tickets.ticketmanagement.tickets.dto.TicketRequestDto;
import com.tickets.ticketmanagement.tickets.entity.Tickets;

public interface TicketService {

    Tickets createTickets(TicketRequestDto ticketRequestDto);
    Tickets findByName(String name);
    Tickets findById(Long id);
    Tickets updateTicket(Long id, TicketRequestDto ticketRequestDto);
    List<Tickets> findAllEvents();
    List<Tickets> findByEventId(Long id);
    Void deleteBy(Long id);
}
