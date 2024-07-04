package com.tickets.ticketmanagement.events.service;

import java.util.List;

import com.tickets.ticketmanagement.events.dto.EventsAllDto;
import com.tickets.ticketmanagement.events.dto.EventsRequestRegisterDto;
import com.tickets.ticketmanagement.events.dto.EventsRequestUpdateDto;
import com.tickets.ticketmanagement.events.dto.EventsResponseDto;
import com.tickets.ticketmanagement.events.entity.Events;
import com.tickets.ticketmanagement.tickets.entity.Tickets;

public interface EventsService {

    EventsResponseDto createEvents(EventsRequestRegisterDto registerDto);
    Events findByName(String name);
    Events findById(Long id);
    Events updateEvents(Long id, EventsRequestUpdateDto eventsRequestUpdateDto);
    List<EventsAllDto> findAllEvents();
    List<EventsAllDto> filterEvents(String location, Long categoryId, Boolean isFree);
    List<Tickets> findTicketsByEventId(Long eventId);
    Void deleteBy(Long id);
}
