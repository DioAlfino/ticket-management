package com.tickets.ticketmanagement.events.dto;

import java.time.Instant;
import java.util.List;

import com.tickets.ticketmanagement.categories.dto.CategoryResponseDto;
import com.tickets.ticketmanagement.promotions.dto.PromotionsDto;
import com.tickets.ticketmanagement.tickets.dto.TicketDto;
import com.tickets.ticketmanagement.users.dto.OrganizerDto;

import lombok.Data;

@Data
public class EventsResponseDto {
    private Long id;
    private String name;
    private Instant date;
    private Instant endDate;
    private String location;
    private String imageUrl;
    private String description;
    private Boolean isFree;
    private OrganizerDto organizer;
    private CategoryResponseDto category;
    private List<TicketDto> tickets;
    private List<PromotionsDto> promotions;
}
