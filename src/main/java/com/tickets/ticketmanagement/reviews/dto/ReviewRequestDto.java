package com.tickets.ticketmanagement.reviews.dto;

import lombok.Data;

@Data
public class ReviewRequestDto {
    private Long userId;
    private Long eventId;
    private Integer rating;
    private String comment;

}
