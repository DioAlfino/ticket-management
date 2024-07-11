package com.tickets.ticketmanagement.events.service;

import java.util.List;

import com.tickets.ticketmanagement.events.dto.EventsAllDto;
import com.tickets.ticketmanagement.events.dto.EventsRequestRegisterDto;
import com.tickets.ticketmanagement.events.dto.EventsRequestUpdateDto;
import com.tickets.ticketmanagement.events.dto.EventsResponseDto;
import com.tickets.ticketmanagement.tickets.entity.Tickets;

public interface EventsService {

    EventsResponseDto createEvents(EventsRequestRegisterDto registerDto);
    List<EventsAllDto> findByName(String name);
    // Events findById(Long id);
    EventsResponseDto updateEvents(Long id, EventsRequestUpdateDto eventsRequestUpdateDto);
    List<EventsAllDto> findAllEvents();
    List<EventsAllDto> filterEvents(String location, Long categoryId, Boolean isFree);
    List<Tickets> findTicketsByEventId(Long eventId);
    void deleteBy(Long id);
}
