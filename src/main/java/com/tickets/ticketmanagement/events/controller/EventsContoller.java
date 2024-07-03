package com.tickets.ticketmanagement.events.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tickets.ticketmanagement.events.dto.EventsRequestRegisterDto;
import com.tickets.ticketmanagement.events.dto.EventsRequestUpdateDto;
import com.tickets.ticketmanagement.events.dto.EventsResponseDto;
import com.tickets.ticketmanagement.events.entity.Events;
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
    public ResponseEntity<?> createEvent(@RequestBody EventsRequestRegisterDto eventsRequestRegisterDto) {
        EventsResponseDto createdEvent = eventsService.createEvents(eventsRequestRegisterDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEvent);
    }

     @PutMapping("/{id}")
    public ResponseEntity<Events> updateEvent(@PathVariable Long id, @ModelAttribute EventsRequestUpdateDto eventsRequestRegisterDto) {
        try {
            Events updateEvents = eventsService.updateEvents(id, eventsRequestRegisterDto);
            return ResponseEntity.ok(updateEvents);
        } catch(RuntimeException e ) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<Response<Events>> getEventByName(@PathVariable String name) {
        Events events = eventsService.findByName(name);
        if (events != null) {
            return Response.success(HttpStatus.OK.value(), "events fetched successfully", events);
        }  
            return Response.failed(HttpStatus.NOT_FOUND.value(), "events with email " + events + " not found");
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response<Events>> getEventById(@PathVariable Long id) {
        Events events = eventsService.findById(id);
        if (events != null) {
            return Response.success("events fetched successfully", events);
        }
            return Response.failed(HttpStatus.NOT_FOUND.value(), "events with id " + id + " not found");
    }

    @GetMapping("/all")
    public ResponseEntity<?> findAllEvent() {
    return Response.success("all events fatched succcessfully", eventsService.findAllEvents());
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
    public ResponseEntity<Response<List<Events>>> filterEvents (
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate starDate,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
        @RequestParam(required = false) String location,
        @RequestParam(required = false) Long categoryId,
        @RequestParam(required = false) Boolean isFree 
    ) {
        List<Events> events = eventsService.filterEvents(starDate, endDate, location, categoryId, isFree);
        return Response.success("events fatced successfully", events);
    }

    @GetMapping("/ticket/{id}")
    public ResponseEntity<List<Tickets>> getTicketTiers(@PathVariable("id") Long id) {
        List<Tickets> ticketTiers = eventsService.findTicketsByEventId(id);
        return ResponseEntity.ok(ticketTiers);
    }
}
