package com.tickets.ticketmanagement.events.dto;

import java.time.Instant;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.tickets.ticketmanagement.tickets.dto.TicketRequestDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EventsRequestRegisterDto {

    @NotBlank(message = "Name is required")
    @Size(max = 255, message = "Name must be less than 255 characters")
    private String name;

    @NotBlank(message = "Location is required")
    private String description;
    
    @NotBlank(message = "Location is required")
    private String location;

    @NotNull(message = "Date is required")
    private  Instant date;;

    @NotBlank(message = "Event type is required")
    @Size(max = 50, message = "Event type must be less than 50 characters")
    private Boolean isFree;

    @NotNull(message = "Category ID is required")
    @Positive(message = "Category ID must be positive")
    private Long categoryId;

    MultipartFile imageUrl;

    private List<TicketRequestDto> tickets;
}
