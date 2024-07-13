package com.tickets.ticketmanagement.tickets.dto;

import lombok.Data;

@Data
public class TicketDto {
    private String tierName;
    private double price;
    private int availableSeats;
    private int maxUser;

    public TicketDto(String tierName, double price, int availableSeats, int maxUser) {
        this.tierName = tierName;
        this.price = price;
        this.availableSeats = availableSeats;
        this.maxUser = maxUser;
    }
}

