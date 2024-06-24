package com.tickets.ticketmanagement.events.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tickets.ticketmanagement.categories.entity.Categories;
import com.tickets.ticketmanagement.events.dto.EventsRequestRegisterDto;
import com.tickets.ticketmanagement.events.dto.EventsRequestUpdateDto;
import com.tickets.ticketmanagement.events.entity.Events;
import com.tickets.ticketmanagement.events.service.EventsService;
import com.tickets.ticketmanagement.response.Response;

@RestController
@RequestMapping("/api/v1/events")
public class EventsContoller {

    private final EventsService eventsService;

    public EventsContoller(EventsService eventsService) {
        this.eventsService = eventsService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createEvent(@RequestBody EventsRequestRegisterDto eventsRequestRegisterDto) {
        return Response.success("user registered successfully", eventsService.createEvents(eventsRequestRegisterDto));
    }

     @PutMapping("/{id}/event")
    public ResponseEntity<Events> updateEvent(@PathVariable Long id, @RequestBody EventsRequestUpdateDto eventsRequestRegisterDto) {
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

    @GetMapping
    public ResponseEntity<?> findAllEvent() {
    return Response.success("all usr fatched succcessfully", eventsService.findAllEvents());
   } 

   @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            eventsService.deleteBy(id);
            return Response.success("Event deleted successfully");
        } catch (RuntimeException e) {
            return Response.failed(HttpStatus.NOT_FOUND.value(), "Event with ID " + id + " not found");
        }
    }

    @GetMapping("/filterByDate")
    public List<Events> filterByDate (
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate starDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return eventsService.filterByDate(starDate, endDate);
    }

    @GetMapping("/filterByLocation")
    public List<Events> filterByLocation (@RequestParam String location) {
        return eventsService.filterByLocation(location);
    }

    @GetMapping("/filterByCategory")
    public List<Events> filterByCategory (@RequestParam Long categoryId) {
        Categories categories = new Categories();
        categories.setId(categoryId);
        return eventsService.filterByCategory(categories);
    }

    @GetMapping("/filterIsFree")
    public List<Events> filterIsFree (@RequestParam Boolean isFree) {
        return eventsService.filterByIsFree(isFree);
    }
}
