package com.tickets.ticketmanagement.events.dto;

import java.time.Instant;


import lombok.Data;

@Data
public class EventsAllDto {

     private Long id;
    private String name;
    private String location;
    private Instant date;
    private String imageUrl;
    private String categoryName;
}
