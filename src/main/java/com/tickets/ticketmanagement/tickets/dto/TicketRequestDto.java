package com.tickets.ticketmanagement.tickets.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TicketRequestDto {

    @NotBlank(message = "Name is required")
    @Size(max = 255, message = "Name must be less than 255 characters")
    private String tierName;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    private Double price;

    @NotNull(message = "Available seats are required")
    @Positive(message = "Available seats must be positive")
    private Integer availableSeats;

    @NotNull(message = "event ID is required")
    @Positive(message = "event ID must be positive")
    private Long eventId;
}
