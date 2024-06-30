package com.tickets.ticketmanagement.events.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.tickets.ticketmanagement.categories.entity.Categories;
import com.tickets.ticketmanagement.events.dto.EventsRequestRegisterDto;
import com.tickets.ticketmanagement.events.dto.EventsRequestUpdateDto;
import com.tickets.ticketmanagement.events.entity.Events;
import com.tickets.ticketmanagement.events.repository.EventsRepository;
import com.tickets.ticketmanagement.events.service.EventsService;
import com.tickets.ticketmanagement.exception.DataNotFoundException;
import com.tickets.ticketmanagement.tickets.entity.Tickets;
import com.tickets.ticketmanagement.tickets.repository.TicketRepository;
import com.tickets.ticketmanagement.users.entity.User;
import com.tickets.ticketmanagement.users.service.UserService;

@Service
public class EventsSerivceImpl implements EventsService {

    private final EventsRepository eventsRepository;
    private final Cloudinary cloudinary; 
    private UserService userService;
    private final TicketRepository ticketRepository;

    public EventsSerivceImpl(EventsRepository eventsRepository, com.cloudinary.Cloudinary cloudinary, UserService userService, TicketRepository ticketRepository) {
        this.eventsRepository = eventsRepository;
        this.userService = userService;
        this.cloudinary = cloudinary;
        this.ticketRepository = ticketRepository;

    }

    @Override
    public Events createEvents(EventsRequestRegisterDto registerDto) {
        Events events = new Events();
        events.setName(registerDto.getName());
        events.setDescription(registerDto.getDescription());
        events.setLocation(registerDto.getLocation());
        events.setDate(registerDto.getDate());
        events.setTime(registerDto.getTime());
        events.setIsFree(registerDto.getIsFree());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        User currentUser = userService.findByEmail(currentUsername);
        events.setOrganizerId(currentUser);

        // User user = new User();
        // user.setId(registerDto.getOrganizerId());

        Categories categories = new Categories();
        categories.setId(registerDto.getCategoryId());
        events.setCategoryId(categories);

        MultipartFile photo = registerDto.getPhoto();
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
    public Events findByName(String name) {
        return eventsRepository.findByName(name).orElse(null);
    }

    @Override
    public Events findById(Long id) {
        return eventsRepository.findById(id).orElseThrow(()-> new RuntimeException("user with id " + id + "not found"));
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
        events.setTime(eventsRequestUpdateDto.getTime());
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
        eventsRepository.deleteById(id);
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
