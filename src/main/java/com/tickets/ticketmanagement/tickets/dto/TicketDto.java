package com.tickets.ticketmanagement.tickets.dto;

import lombok.Data;

@Data
public class TicketDto {
    private String tierName;
    private double price;
    private int availableSeats;

    public TicketDto(String tierName, double price, int availableSeats) {
        this.tierName = tierName;
        this.price = price;
        this.availableSeats = availableSeats;
    }
}

