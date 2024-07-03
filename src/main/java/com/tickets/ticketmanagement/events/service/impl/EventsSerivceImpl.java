package com.tickets.ticketmanagement.events.service.impl;

import java.time.LocalDate;
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
import com.tickets.ticketmanagement.categories.dto.CategoryResponseDto;
import com.tickets.ticketmanagement.categories.entity.Categories;
import com.tickets.ticketmanagement.categories.repository.CategoriesRepository;
import com.tickets.ticketmanagement.events.dto.EventsRequestRegisterDto;
import com.tickets.ticketmanagement.events.dto.EventsRequestUpdateDto;
import com.tickets.ticketmanagement.events.dto.EventsResponseDto;
import com.tickets.ticketmanagement.events.entity.Events;
import com.tickets.ticketmanagement.events.repository.EventsRepository;
import com.tickets.ticketmanagement.events.service.EventsService;
import com.tickets.ticketmanagement.exception.DataNotFoundException;
import com.tickets.ticketmanagement.exception.DatabaseOperationException;
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
    private UserService userService;
    private final TicketRepository ticketRepository;
    private final CategoriesRepository categoriesRepository;

    public EventsSerivceImpl(EventsRepository eventsRepository, com.cloudinary.Cloudinary cloudinary, UserService userService, TicketRepository ticketRepository, CategoriesRepository categoriesRepository) {
        this.eventsRepository = eventsRepository;
        this.userService = userService;
        this.cloudinary = cloudinary;
        this.ticketRepository = ticketRepository;
        this.categoriesRepository = categoriesRepository;
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

        // Menyimpan event ke dalam database
        Events savedEvents = eventsRepository.save(events);

        // Simpan tiket-tiket dengan menyesuaikan event ID yang telah disimpan
        List<Tickets> tickets = registerDto.getTickets().stream().map(ticketDto -> {
            Tickets ticket = new Tickets();
            ticket.setTierName(ticketDto.getTierName());
            ticket.setPrice(ticketDto.getPrice());
            ticket.setAvailableSeats(ticketDto.getAvailableSeats());
            ticket.setEvent(savedEvents); // Set event
            return ticket;
        }).collect(Collectors.toList());

        // Simpan semua tiket ke dalam database
        ticketRepository.saveAll(tickets);
        savedEvents.setTickets(tickets);

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
    public List<Events> findAllEvents() {
        return eventsRepository.findAll();
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
    public List<Events> filterEvents(LocalDate startDate, LocalDate endDate, String location, Long categoryId,
        Boolean isFree) {
            return eventsRepository.filterEvents(startDate, endDate, location, categoryId, isFree);
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
