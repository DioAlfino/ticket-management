package com.tickets.ticketmanagement.reviews.controller;

import com.tickets.ticketmanagement.reviews.dto.ReviewRequestDto;
import com.tickets.ticketmanagement.reviews.service.ReviewService;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public ResponseEntity<ReviewRequestDto> createReview(@Valid @RequestBody ReviewRequestDto reviewRequestDto, Authentication authentication) {
        ReviewRequestDto createdReview = reviewService.createReview(
                reviewRequestDto.getEventId(),
                reviewRequestDto.getUserId(),
                reviewRequestDto.getRating(),
                reviewRequestDto.getComment()
        );
        return new ResponseEntity<>(createdReview, HttpStatus.CREATED);
    }
}