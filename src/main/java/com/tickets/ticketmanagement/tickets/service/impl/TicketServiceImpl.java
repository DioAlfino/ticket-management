package com.tickets.ticketmanagement.tickets.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.tickets.ticketmanagement.events.entity.Events;
import com.tickets.ticketmanagement.events.repository.EventsRepository;
import com.tickets.ticketmanagement.tickets.dto.TicketRequestDto;
import com.tickets.ticketmanagement.tickets.entity.Tickets;
import com.tickets.ticketmanagement.tickets.repository.TicketRepository;
import com.tickets.ticketmanagement.tickets.service.TicketService;

@Service
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final EventsRepository eventsRepository;

    public TicketServiceImpl(TicketRepository ticketRepository, EventsRepository eventsRepository) {
        this.ticketRepository = ticketRepository;
        this.eventsRepository = eventsRepository;
    }

    @Override
    public Tickets createTickets(TicketRequestDto ticketRequestDto) {
        Tickets tickets = new Tickets();
        tickets.setTicketName(ticketRequestDto.getTierName());
        tickets.setPrice(ticketRequestDto.getPrice());
        tickets.setAvailableSeats(ticketRequestDto.getAvailableSeats());
        
        Events events = new Events();
        events.setId(ticketRequestDto.getEventId());

        tickets.setEventId(events);
        return ticketRepository.save(tickets);
    }

    @Override
    public Tickets findByName(String name) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findByName'");
    }

    @Override
    public Tickets findById(Long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findById'");
    }

    @Override
    public Tickets updateTicket(Long id, TicketRequestDto ticketRequestDto) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateEvents'");
    }

    @Override
    public List<Tickets> findAllEvents() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findAllEvents'");
    }

    @Override
    public Void deleteBy(Long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteBy'");
    }

}
