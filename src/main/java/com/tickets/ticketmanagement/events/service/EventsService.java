package com.tickets.ticketmanagement.events.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.tickets.ticketmanagement.events.dto.EventsAllDto;
import com.tickets.ticketmanagement.events.dto.EventsRequestRegisterDto;
import com.tickets.ticketmanagement.events.dto.EventsRequestUpdateDto;
import com.tickets.ticketmanagement.events.dto.EventsResponseDto;
import com.tickets.ticketmanagement.tickets.entity.Tickets;

public interface EventsService {

    EventsResponseDto createEvents(EventsRequestRegisterDto registerDto);
    List<EventsAllDto> findByName(String name);
    EventsResponseDto getEventDetails(Long eventId);
    EventsResponseDto updateEvents(Long id, EventsRequestUpdateDto eventsRequestUpdateDto);
    // List<EventsAllDto> findAllEvents();
    Page<EventsAllDto> findAllEvents(Pageable pageable);
    List<EventsAllDto> filterEvents(String location, Long categoryId, Boolean isFree);
    List<Tickets> findTicketsByEventId(Long eventId);
    void deleteBy(Long id);
}
