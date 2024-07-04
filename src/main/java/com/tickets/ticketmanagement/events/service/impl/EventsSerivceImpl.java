package com.tickets.ticketmanagement.events.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
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
import com.tickets.ticketmanagement.exception.DatabaseOperationException;
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
    public Events updateEvents(Long id, EventsRequestUpdateDto eventsRequestUpdateDto) {
        Events events = eventsRepository.findById(id).orElseThrow(() -> new DataNotFoundException("Event not found with id " + id ));
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        User currentUser = userService.findByEmail(currentUsername);
        if (!events.getOrganizerId().equals(currentUser)) {
            throw new DataNotFoundException("you are not authorizerd to update this event");
        }

        events.setName(eventsRequestUpdateDto.getName());
        events.setDescription(eventsRequestUpdateDto.getDescription());
        events.setLocation(eventsRequestUpdateDto.getLocation());
        events.setDate(eventsRequestUpdateDto.getDate());
        events.setIsFree(eventsRequestUpdateDto.getIsFree());

        Categories categories = new Categories();
        categories.setId(eventsRequestUpdateDto.getCategoryId());
        events.setCategoryId(categories);

        MultipartFile photo = eventsRequestUpdateDto.getPhoto();
        if (photo != null && !photo.isEmpty()) {
            try {
                if (events.getPhotoUrl() != null) {
                    cloudinary.uploader().destroy(getPublicIdFromUrl(events.getPhotoUrl()), ObjectUtils.emptyMap());
                }
                @SuppressWarnings("unchecked")
                Map<String, Object> uploadResult = cloudinary.uploader().upload(photo.getBytes(), ObjectUtils.emptyMap());
                events.setPhotoUrl((String) uploadResult.get("url"));
            } catch (Exception e) {
                throw new RuntimeException("Photo upload failed", e);
            }
        }
         return eventsRepository.save(events);
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
        return dto;
    }

    @Override
    public Void deleteBy(Long id) {
        if (!eventsRepository.existsById(id)) {
            throw new DataNotFoundException("user with id " + id + " not found");
        } try{
            eventsRepository.deleteById(id);
        } catch(DataIntegrityViolationException ex) {
            throw new DatabaseOperationException("Failed to delete user due to database error", ex);
    }
        return null;
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

    private String getPublicIdFromUrl(String photoUrl) {
        String[] components = photoUrl.split("/");
        String publicWithFormat = components[components.length - 1];
        return publicWithFormat.substring(0, publicWithFormat.lastIndexOf("."));
    }
}
