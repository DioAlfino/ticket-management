package com.tickets.ticketmanagement.reviews.service;

import com.tickets.ticketmanagement.events.entity.Events;
import com.tickets.ticketmanagement.events.repository.EventsRepository;
import com.tickets.ticketmanagement.exception.DataNotFoundException;
import com.tickets.ticketmanagement.reviews.dto.ReviewRequestDto;
import com.tickets.ticketmanagement.reviews.entity.Reviews;
import com.tickets.ticketmanagement.reviews.repository.ReviewRepository;
import com.tickets.ticketmanagement.users.entity.User;
import com.tickets.ticketmanagement.users.repository.UserRepository;

import java.time.Instant;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final EventsRepository eventsRepository;

    public ReviewService(ReviewRepository reviewRepository, UserRepository userRepository, EventsRepository eventsRepository) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.eventsRepository = eventsRepository;
    }

    @Transactional
    public ReviewRequestDto createReview(Long eventId, Long long1, Integer rating, String comment) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();

        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new DataNotFoundException("User not found"));

        Events event = eventsRepository.findById(eventId)
                .orElseThrow(() -> new DataNotFoundException("Event not found"));

        if (event.getEndDate().isAfter(Instant.now())) {
            throw new IllegalStateException("Cannot review an event that has not ended yet");
        }

        if (reviewRepository.existsByEvents_IdAndUser_Email(eventId, currentUserEmail)) {
            throw new IllegalStateException("User has already reviewed this event");
        }

        Reviews review = new Reviews();
        review.setUser(currentUser);
        review.setEvents(event);
        review.setRating(rating);
        review.setComment(comment);

        Reviews savedReviews = reviewRepository.save(review);
        return convertToDto(savedReviews);
    }

    private ReviewRequestDto convertToDto (Reviews reviews) {
        ReviewRequestDto dto = new ReviewRequestDto();
        dto.setUserId(reviews.getUser().getId());
        dto.setEventId(reviews.getId());
        dto.setComment(reviews.getComment());
        dto.setRating(reviews.getRating());

        return dto;
    }
}
