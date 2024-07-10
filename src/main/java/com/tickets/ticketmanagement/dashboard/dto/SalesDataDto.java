package com.tickets.ticketmanagement.dashboard.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class SalesDataDto {

    private Long eventId;
    private LocalDate date;
    private Long totalTicketsSold;

    public SalesDataDto (Long eventId, LocalDate date, Long totalTicketsSold) {
        this.eventId =eventId;
        this.date = date;
        this.totalTicketsSold = totalTicketsSold;
    }
}
