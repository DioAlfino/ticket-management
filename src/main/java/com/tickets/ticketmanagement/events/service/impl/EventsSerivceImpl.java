package com.tickets.ticketmanagement.events.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

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

        // Konversi entitas yang disimpan ke dalam DTO respons
        EventsResponseDto responseDto = convertToDto(savedEvents);

        return responseDto;
    }

public EventsResponseDto convertToDto(Events events) {
        EventsResponseDto responseDto = new EventsResponseDto();
        responseDto.setId(events.getId());
        responseDto.setName(events.getName());
        responseDto.setDate(events.getDate());
        responseDto.setLocation(events.getLocation());
        responseDto.setDescription(events.getDescription());
        responseDto.setIsFree(events.getIsFree());
        
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
            ticketDtos = events.getTickets().stream().map(ticket -> new TicketDto(ticket.getTierName(), ticket.getPrice(), ticket.getAvailableSeats())).collect(Collectors.toList());
        }
        responseDto.setTickets(ticketDtos);

        List<PromotionsDto> promotionsDtos = new ArrayList<>();
        if (events.getPromotions() != null && !events.getPromotions().isEmpty()) {
            promotionsDtos = events.getPromotions().stream().map(promotion -> new PromotionsDto(promotion.getName(), promotion.getDiscount(), promotion.getMaxUser())).collect(Collectors.toList());
        } 
        responseDto.setPromotions(promotionsDtos);

        return responseDto;
    }

    @Override
    public Events findByName(String name) {
        return eventsRepository.findByName(name).orElseThrow(() -> new DataNotFoundException("user with email " + name + " not found"));
    }

    @Override
    public Events findById(Long id) {
        return eventsRepository.findById(id).orElseThrow(() -> new DataNotFoundException("user with id " + id + " not found"));
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

        List<Tickets> newTickets;
        try {
            newTickets = objectMapper.readValue(eventsRequestUpdateDto.getTickets(), new TypeReference<List<Tickets>>() {});
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse tickets JSON", e);
        }

        Map<Long, Tickets> existingTicketsMap = existingEvent.getTickets().stream()
            .collect(Collectors.toMap(Tickets::getId, Function.identity()));

        Set<Long> updateTicketIds = new HashSet<>();

        for (Tickets newTicket : newTickets) {
        if (newTicket.getId() != null && existingTicketsMap.containsKey(newTicket.getId())) {
            Tickets existingTicket = existingTicketsMap.get(newTicket.getId());
            existingTicket.setTierName(newTicket.getTierName());
            existingTicket.setPrice(newTicket.getPrice());
            existingTicket.setAvailableSeats(newTicket.getAvailableSeats());
            existingTicket.setMaxUser(newTicket.getMaxUser());
            updateTicketIds.add(existingTicket.getId());
        } else {
            newTicket.setEvent(existingEvent);
            existingEvent.getTickets().add(newTicket);
            updateTicketIds.add(newTicket.getId());
            }
        }

        existingEvent.getTickets().removeIf(ticket -> !updateTicketIds.contains(ticket.getId()));
        
        List<Promotions> newPromotions;
        try {
            newPromotions = objectMapper.readValue(eventsRequestUpdateDto.getPromotions(), new TypeReference<List<Promotions>>() {});
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse promotions json", e);
        }

        Map<Long, Promotions> existingPromotionsMap = existingEvent.getPromotions().stream()
            .collect(Collectors.toMap(Promotions::getId, Function.identity()));

        Set<Long> updatePromotionIds = new HashSet<>();

        for (Promotions newPromotion : newPromotions) {
            if (newPromotion.getId() != null && existingPromotionsMap.containsKey(newPromotion.getId())) {
                Promotions existingPromotion = existingPromotionsMap.get(newPromotion.getId());
                existingPromotion.setName(newPromotion.getName());
                existingPromotion.setDiscount(newPromotion.getDiscount());
                existingPromotion.setMaxUser(newPromotion.getMaxUser());
                updatePromotionIds.add(existingPromotion.getId());
            } else {
                newPromotion.setEventId(existingEvent);
                existingEvent.getPromotions().add(newPromotion);
                updatePromotionIds.add(newPromotion.getId());
            }
        }

        existingEvent.getPromotions().removeIf(promotion -> !updatePromotionIds.contains(promotion.getId()));

        Events updateEvent = eventsRepository.save(existingEvent);
        EventsResponseDto responseDto = convertToDto(updateEvent);
        return responseDto;
    }

    @Override
    public List<EventsAllDto> findAllEvents() {
        List<Events> events = eventsRepository.findAll();
        return events.stream().map(this::EventConvertToDto).collect(Collectors.toList());
    }

    private EventsAllDto EventConvertToDto(Events events) {
        EventsAllDto dto = new EventsAllDto();
        dto.setId(events.getId());
        dto.setName(events.getName());
        dto.setLocation(events.getLocation());
        dto.setDate(events.getDate());
        dto.setImageUrl(events.getPhotoUrl());
        dto.setCategoryName(events.getCategoryId().getName());

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
    public List<EventsAllDto> filterEvents(String location, Long categoryId, Boolean isFree) {
        List<Events> events = eventsRepository.filterEvents(location, categoryId, isFree);
        return events.stream().map(this::convertFilterEventsToDto).collect(Collectors.toList());
    }

    private EventsAllDto convertFilterEventsToDto (Events events) {
        EventsAllDto dto = new EventsAllDto();
        dto.setId(events.getId());
        dto.setName(events.getName());
        dto.setLocation(events.getLocation());
        dto.setImageUrl(events.getPhotoUrl());
        return dto;
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
