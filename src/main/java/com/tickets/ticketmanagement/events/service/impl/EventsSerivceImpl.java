package com.tickets.ticketmanagement.events.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tickets.ticketmanagement.categories.dto.CategoryResponseDto;
import com.tickets.ticketmanagement.categories.entity.Categories;
import com.tickets.ticketmanagement.categories.repository.CategoriesRepository;
import com.tickets.ticketmanagement.events.dto.EventsAllDto;
import com.tickets.ticketmanagement.events.dto.EventsRequestRegisterDto;
import com.tickets.ticketmanagement.events.dto.EventsRequestUpdateDto;
import com.tickets.ticketmanagement.events.dto.EventsResponseDto;
import com.tickets.ticketmanagement.events.entity.Events;
import com.tickets.ticketmanagement.events.repository.EventsRepository;
import com.tickets.ticketmanagement.events.service.EventsService;
import com.tickets.ticketmanagement.exception.DataNotFoundException;
import com.tickets.ticketmanagement.promotions.dto.PromotionsDto;
import com.tickets.ticketmanagement.promotions.entity.Promotions;
import com.tickets.ticketmanagement.promotions.repository.PromotionsRepository;
import com.tickets.ticketmanagement.tickets.dto.TicketDto;
import com.tickets.ticketmanagement.tickets.entity.Tickets;
import com.tickets.ticketmanagement.tickets.repository.TicketRepository;
import com.tickets.ticketmanagement.users.dto.OrganizerDto;
import com.tickets.ticketmanagement.users.entity.User;
import com.tickets.ticketmanagement.users.service.UserService;


@Service
public class EventsSerivceImpl implements EventsService {

    private final EventsRepository eventsRepository;
    private final Cloudinary cloudinary; 
    private final UserService userService;
    private final TicketRepository ticketRepository;
    private final CategoriesRepository categoriesRepository;
    private final PromotionsRepository promotionsRepository;
    private  final ObjectMapper objectMapper;

    public EventsSerivceImpl(EventsRepository eventsRepository, com.cloudinary.Cloudinary cloudinary, UserService userService, TicketRepository ticketRepository, CategoriesRepository categoriesRepository, PromotionsRepository promotionsRepository, ObjectMapper objectMapper) {
        this.eventsRepository = eventsRepository;
        this.userService = userService;
        this.cloudinary = cloudinary;
        this.ticketRepository = ticketRepository;
        this.categoriesRepository = categoriesRepository;
        this.promotionsRepository = promotionsRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional
    public EventsResponseDto createEvents(EventsRequestRegisterDto registerDto) {
        // Membuat objek Events dari data yang diterima dari DTO
        Events events = new Events();
        events.setName(registerDto.getName());
        events.setDate(registerDto.getDate());
        events.setEndDate(registerDto.getEndDate());
        events.setLocation(registerDto.getLocation());
        events.setDescription(registerDto.getDescription());
        events.setIsFree(registerDto.getIsFree());

        // Mengambil username saat ini dari konteks keamanan
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        // Mencari user berdasarkan email
        User currentUser = userService.findByEmail(currentUsername);
        if (currentUser == null) {
            throw new DataNotFoundException("Current user not found with email: " + currentUsername);
        }
        events.setOrganizerId(currentUser);

        // Mencari kategori berdasarkan ID
        Categories category = categoriesRepository.findById(registerDto.getCategoryId())
                .orElseThrow(() -> new DataNotFoundException("Category not found with id: " + registerDto.getCategoryId()));
        events.setCategoryId(category);

        MultipartFile imageFile = registerDto.getImageUrl();
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                @SuppressWarnings("unchecked")
                Map<String, Object> uploadResult = cloudinary.uploader().upload(imageFile.getBytes(), ObjectUtils.emptyMap());
                events.setPhotoUrl((String) uploadResult.get("url"));
            } catch (IOException e) {
                throw new RuntimeException("Photo upload failed", e);
            }
        }

        // Menyimpan event ke dalam database
        Events savedEvents = eventsRepository.save(events);

        if(registerDto.getIsFree()) {
            Tickets freeTicket = new Tickets();
            freeTicket.setEvent(savedEvents);
            freeTicket.setPrice(0.0);
            freeTicket.setTierName("Free Ticket");
            freeTicket.setAvailableSeats(registerDto.getAvailableSeats());

            List<Tickets> tickets = new ArrayList<>();
            tickets.add(freeTicket);
            ticketRepository.saveAll(tickets);
            savedEvents.setTickets(tickets);

            savedEvents.setPromotions(Collections.emptyList());
        } else {
            // Simpan tiket-tiket dengan menyesuaikan event ID yang telah disimpan
            List<Tickets> tickets;
            try {
                tickets = objectMapper.readValue(registerDto.getTickets(), new TypeReference<List<Tickets>>() {});
            } catch (IOException e) {
                throw new RuntimeException("Failed to parse ticktes JSOn", e);
            }

            tickets.forEach(ticket -> ticket.setEvent(savedEvents));
            ticketRepository.saveAll(tickets);
            savedEvents.setTickets(tickets);

            List<Promotions> promotionList;
            try {
                promotionList = objectMapper.readValue(registerDto.getPromotions(), new TypeReference<List<Promotions>>() {});
            } catch (IOException e) {
                throw new RuntimeException("failed to parse promotiosn JSON", e);
            }

            promotionList.forEach(promotion -> promotion.setEventId(savedEvents));
            promotionsRepository.saveAll(promotionList);
            savedEvents.setPromotions(promotionList);
        }
        // Konversi entitas yang disimpan ke dalam DTO respons
        EventsResponseDto responseDto = convertToDto(savedEvents);

        return responseDto;
    }

public EventsResponseDto convertToDto(Events events) {
        EventsResponseDto responseDto = new EventsResponseDto();
        responseDto.setId(events.getId());
        responseDto.setName(events.getName());
        responseDto.setDate(events.getDate());
        responseDto.setEndDate(events.getEndDate());
        responseDto.setLocation(events.getLocation());
        responseDto.setDescription(events.getDescription());
        responseDto.setIsFree(events.getIsFree());
        responseDto.setImageUrl(events.getPhotoUrl());
        
        // Konversi organizer
        OrganizerDto organizerDto = new OrganizerDto();
        organizerDto.setId(events.getOrganizerId().getId());
        organizerDto.setName(events.getOrganizerId().getName());
        organizerDto.setEmail(events.getOrganizerId().getEmail());
        responseDto.setOrganizer(organizerDto);
        
        // Konversi category
        CategoryResponseDto categoryDto = new CategoryResponseDto();
        categoryDto.setId(events.getCategoryId().getId());
        categoryDto.setName(events.getCategoryId().getName());
        responseDto.setCategory(categoryDto);

        List<TicketDto> ticketDtos = new ArrayList<>();
        if (events.getTickets() != null && !events.getTickets().isEmpty()) {
            ticketDtos = events.getTickets().stream().map(ticket -> new TicketDto(ticket.getTierName(), ticket.getPrice(), ticket.getAvailableSeats(), ticket.getId())).collect(Collectors.toList());
        }
        responseDto.setTickets(ticketDtos);

        List<PromotionsDto> promotionsDtos = new ArrayList<>();
        if (events.getPromotions() != null && !events.getPromotions().isEmpty()) {
            promotionsDtos = events.getPromotions().stream().map(promotion -> new PromotionsDto(promotion.getName(), promotion.getDiscount(), promotion.getMaxUser(), promotion.getId())).collect(Collectors.toList());
        } 
        responseDto.setPromotions(promotionsDtos);

        return responseDto;
    }

    public EventsResponseDto getEventDetails(Long eventId) {
        Events event = eventsRepository.findById(eventId)
                .orElseThrow(() -> new DataNotFoundException("Event not found with id: " + eventId));
        return convertToDto(event);
    }

    @Override
    public List<EventsAllDto> findByName(String name) {
        List<Events> events = eventsRepository.findByNameContainingIgnoreCase(name);
        return events.stream().map(this::EventConvertToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventsResponseDto updateEvents(Long id, EventsRequestUpdateDto eventsRequestUpdateDto) {
        Events existingEvent = eventsRepository.findById(id).orElseThrow(() -> new DataNotFoundException("Event not found with id " + id ));
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        User currentUser = userService.findByEmail(currentUsername);
        if (!existingEvent.getOrganizerId().equals(currentUser)) {
            throw new DataNotFoundException("you are not authorizerd to update this event");
        }
        existingEvent.setName(eventsRequestUpdateDto.getName());
        existingEvent.setDescription(eventsRequestUpdateDto.getDescription());
        existingEvent.setLocation(eventsRequestUpdateDto.getLocation());
        existingEvent.setDate(eventsRequestUpdateDto.getDate());
        existingEvent.setEndDate(eventsRequestUpdateDto.getEndDate());
        existingEvent.setIsFree(eventsRequestUpdateDto.getIsFree());

        Categories category = categoriesRepository.findById(eventsRequestUpdateDto.getCategoryId())
            .orElseThrow(() -> new DataNotFoundException("category not found with id " + eventsRequestUpdateDto.getCategoryId()));
        existingEvent.setCategoryId(category);

        MultipartFile photo = eventsRequestUpdateDto.getPhoto();
        if (photo != null && !photo.isEmpty()) {
            try {
                @SuppressWarnings("unchecked")
                Map<String, Object> uploadResult = cloudinary.uploader().upload(photo.getBytes(), ObjectUtils.emptyMap());
                existingEvent.setPhotoUrl((String) uploadResult.get("url"));
            } catch (IOException e) {
                throw new RuntimeException("photo upload failed", e);
            }
        }

        if (eventsRequestUpdateDto.getIsFree()) {
            if (!existingEvent.getTickets().isEmpty()) {
                Tickets freeTicket = existingEvent.getTickets().get(0);
                freeTicket.setPrice(0.0);
                freeTicket.setTierName("Free Ticket");
                freeTicket.setAvailableSeats(eventsRequestUpdateDto.getAvailableSeats());
            } else {
                Tickets freeTicket = new Tickets();
                freeTicket.setEvent(existingEvent);
                freeTicket.setPrice(0.0);
                freeTicket.setTierName("Free ticket");
                freeTicket.setAvailableSeats(eventsRequestUpdateDto.getAvailableSeats());
                existingEvent.getTickets().add(freeTicket);
            }
            existingEvent.getPromotions().clear();
        } else {
            List<Tickets> newTickets;
            try {
                newTickets = objectMapper.readValue(eventsRequestUpdateDto.getTickets(), new TypeReference<List<Tickets>>() {});
            } catch (IOException e) {
                throw new RuntimeException("Failed to parse tickets JSON", e);
            }

            for (Tickets newTicket : newTickets) {
                Tickets existingTicket = existingEvent.getTickets().stream()
                    .filter(ticket -> ticket.getTierName().equals(newTicket.getTierName()))
                    .findFirst()
                    .orElse(null);
                if (existingTicket != null) {
                    existingTicket.setPrice(newTicket.getPrice());
                    existingTicket.setAvailableSeats(newTicket.getAvailableSeats());
                } else {
                    newTicket.setEvent(existingEvent);
                    ticketRepository.save(newTicket);
                }
            }
            List<Promotions> newPromotions;
            try {
                newPromotions = objectMapper.readValue(eventsRequestUpdateDto.getPromotions(), new TypeReference<List<Promotions>>() {});
            } catch (IOException e) {
                throw new RuntimeException("Failed to parse promotions json", e);
            }

            for (Promotions newPromotion : newPromotions) {
                Promotions existingPromotion = existingEvent.getPromotions().stream()
                    .filter(promotion -> promotion.getName().equals(newPromotion.getName()))
                    .findFirst()
                    .orElse(null);
                if (existingPromotion != null) {
                    existingPromotion.setDiscount(newPromotion.getDiscount());
                    existingPromotion.setMaxUser(newPromotion.getMaxUser());
                } else {
                    newPromotion.setEventId(existingEvent);
                    promotionsRepository.save(newPromotion);
                }
            }
        }

        Events updateEvent = eventsRepository.save(existingEvent);
        EventsResponseDto responseDto = convertToDto(updateEvent);
        return responseDto;
    }

    // @Override
    // public List<EventsAllDto> findAllEvents() {
    //     List<Events> events = eventsRepository.findAll();
    //     return events.stream().map(this::EventConvertToDto).collect(Collectors.toList());
    // }

    @Override
    public Page<EventsAllDto> findAllEvents(Pageable pageable) {
        Page<Events> eventsPage = eventsRepository.findAll(pageable);
        return eventsPage.map(this::EventConvertToDto);
    } 

    private EventsAllDto EventConvertToDto(Events events) {
        EventsAllDto dto = new EventsAllDto();
        dto.setId(events.getId());
        dto.setName(events.getName());
        dto.setLocation(events.getLocation());
        dto.setDate(events.getDate());
        dto.setImageUrl(events.getPhotoUrl());
        
        if (events.getCategoryId() != null) {
            dto.setCategoryName(events.getCategoryId().getName());
        }

        return dto;
    }

    @Override
    @Transactional
    public void deleteBy(Long id) {
        Events event = eventsRepository.findById(id).orElseThrow(() -> new DataNotFoundException("event nott found with id "+ id));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        User currentUser = userService.findByEmail(currentUsername);
        if (!event.getOrganizerId().equals(currentUser)) {
            throw new DataNotFoundException("you are not authorized to delete this event");
        }
        eventsRepository.delete(event);
    }

    @Override
    public List<EventsAllDto> filterEvents(String location, Long categoryId, Boolean isFree, String name) {
        List<Events> events = eventsRepository.filterEvents(location, categoryId, isFree, name);
        return events.stream().map(this::EventConvertToDto).collect(Collectors.toList());
    }

    @Override
    public List<Tickets> findTicketsByEventId(Long eventId) {
        return ticketRepository.allTicketTier(eventId);
    }

    // private String getPublicIdFromUrl(String photoUrl) {
    //     String[] components = photoUrl.split("/");
    //     String publicWithFormat = components[components.length - 1];
    //     return publicWithFormat.substring(0, publicWithFormat.lastIndexOf("."));
    // }
}
