package com.tickets.ticketmanagement.events.service;

import java.util.List;

import com.tickets.ticketmanagement.events.dto.EventsRequestRegisterDto;
import com.tickets.ticketmanagement.events.dto.EventsRequestUpdateDto;
import com.tickets.ticketmanagement.events.entity.Events;

public interface EventsService {

    Events createEvents(EventsRequestRegisterDto registerDto);
    Events findByName(String name);
    Events findById(Long id);
    Events updateEvents(Long id, EventsRequestUpdateDto eventsRequestUpdateDto);
    List<Events> findAllEvents();
    Void deleteBy(Long id);
}
