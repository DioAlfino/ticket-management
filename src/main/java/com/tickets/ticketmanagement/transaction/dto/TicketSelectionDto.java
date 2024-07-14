package com.tickets.ticketmanagement.transaction.dto;

import lombok.Data;

@Data
public class TicketSelectionDto {

    private Long ticketId;
    private int quantity;

    public TicketSelectionDto() {}
}
