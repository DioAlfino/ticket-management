package com.tickets.ticketmanagement.tickets.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tickets.ticketmanagement.response.Response;
import com.tickets.ticketmanagement.tickets.dto.TicketRequestDto;
import com.tickets.ticketmanagement.tickets.service.TicketService;

@RestController
@RequestMapping("/api/v1/ticket")
public class TicketController {
    private final TicketService ticketService;

    public TicketController (TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createTicket(@RequestBody TicketRequestDto ticketRequestDto) {
        return Response.success("user registered successfully", ticketService.createTickets(ticketRequestDto));
    }
}
