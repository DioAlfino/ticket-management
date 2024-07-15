package com.tickets.ticketmanagement.events.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tickets.ticketmanagement.events.dto.EventsAllDto;
import com.tickets.ticketmanagement.events.dto.EventsRequestRegisterDto;
import com.tickets.ticketmanagement.events.dto.EventsRequestUpdateDto;
import com.tickets.ticketmanagement.events.dto.EventsResponseDto;
import com.tickets.ticketmanagement.events.service.EventsService;
import com.tickets.ticketmanagement.response.Response;
import com.tickets.ticketmanagement.tickets.entity.Tickets;


@RestController
@RequestMapping("/api/v1/events")
public class EventsContoller {

    private final EventsService eventsService;

    public EventsContoller(EventsService eventsService) {
        this.eventsService = eventsService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createEvent(@ModelAttribute EventsRequestRegisterDto eventsRequestRegisterDto) {
        EventsResponseDto createdEvent = eventsService.createEvents(eventsRequestRegisterDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEvent);
    }

     @PutMapping("/{id}")
    public ResponseEntity<?> updateEvent(@PathVariable Long id, @ModelAttribute EventsRequestUpdateDto eventsRequestRegisterDto) {
        EventsResponseDto updateEvent = eventsService.updateEvents(id, eventsRequestRegisterDto);
        return ResponseEntity.status(HttpStatus.OK).body(updateEvent);
    }

    @GetMapping("/{name}")
    public ResponseEntity<Response<List<EventsAllDto>>> getEventByName(@PathVariable String name) {
        List<EventsAllDto> events = eventsService.findByName(name);
        if (!events.isEmpty()) {
            return Response.success(HttpStatus.OK.value(), "Events fetched successfully", events);
        }
        return Response.failed(HttpStatus.NOT_FOUND.value(), "Events with name " + name + " not found");
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<EventsResponseDto> getEventDetails(@PathVariable Long id) {
        EventsResponseDto eventDetails = eventsService.getEventDetails(id);
        return ResponseEntity.ok(eventDetails);
    }

    @GetMapping("")
    public ResponseEntity<?> findAllEvent(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue ="8") int size,
        @RequestParam(defaultValue = "id.asc") String[] sort) {

            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.by(sort[0]).with(Sort.Direction.fromString(sort[1]))));
            Page<EventsAllDto> eventsPage = eventsService.findAllEvents(pageable);
            
            return Response.success(HttpStatus.OK.value(), "all events fetched successfully", eventsPage.getContent(), eventsPage.getTotalPages(), eventsPage.getTotalElements());
    }
        
   @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEvent(@PathVariable Long id) {
        try {
            eventsService.deleteBy(id);
            return Response.success("Event deleted successfully");
        } catch (RuntimeException e) {
            return Response.failed(HttpStatus.NOT_FOUND.value(), "Event with ID " + id + " not found");
        }
    }

    @GetMapping("/")
    public ResponseEntity<Response<List<EventsAllDto>>> filterEvents (
        @RequestParam(required = false) String location,
        @RequestParam(required = false) Long categoryId,
        @RequestParam(required = false) Boolean isFree,
        @RequestParam(required = false) String name 
    ) {
        List<EventsAllDto> events = eventsService.filterEvents(location, categoryId, isFree, name);
        return Response.success("events fatced successfully", events);
    }

    @GetMapping("/ticket/{id}")
    public ResponseEntity<List<Tickets>> getTicketTiers(@PathVariable("id") Long id) {
        List<Tickets> ticketTiers = eventsService.findTicketsByEventId(id);
        return ResponseEntity.ok(ticketTiers);
    }
}
