package com.tickets.ticketmanagement.tickets.dto;

import lombok.Data;

@Data
public class TicketRequestDto {

    private String tierName;
    private Double price;
    private Integer availableSeats;
    private Integer maxUser;
}
