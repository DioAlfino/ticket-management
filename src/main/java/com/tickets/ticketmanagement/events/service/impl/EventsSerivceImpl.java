package com.tickets.ticketmanagement.events.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.tickets.ticketmanagement.categories.entity.Categories;
import com.tickets.ticketmanagement.categories.repository.CategoriesRepository;
import com.tickets.ticketmanagement.events.dto.EventsRequestRegisterDto;
import com.tickets.ticketmanagement.events.dto.EventsRequestUpdateDto;
import com.tickets.ticketmanagement.events.entity.Events;
import com.tickets.ticketmanagement.events.repository.EventsRepository;
import com.tickets.ticketmanagement.events.service.EventsService;
import com.tickets.ticketmanagement.users.entity.User;
import com.tickets.ticketmanagement.users.repository.UserRepository;
import com.tickets.ticketmanagement.users.service.UserService;

@Service
public class EventsSerivceImpl implements EventsService {

    private final EventsRepository eventsRepository;
    private final UserRepository userRepository;
    private final CategoriesRepository categoriesRepository;
    private final Cloudinary cloudinary; 
    private UserService userService;

    public EventsSerivceImpl(EventsRepository eventsRepository, UserRepository userRepository, CategoriesRepository categoriesRepository, com.cloudinary.Cloudinary cloudinary, UserService userService) {
        this.eventsRepository = eventsRepository;
        this.userRepository = userRepository;
        this.categoriesRepository = categoriesRepository;
        this.userService = userService;
        this.cloudinary = cloudinary;

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
        Optional<Events> optionalEvents = eventsRepository.findById(id);
        if (optionalEvents.isPresent()) {
            Events events = optionalEvents.get();
            events.setName(eventsRequestUpdateDto.getName());
            events.setDescription(eventsRequestUpdateDto.getDescription());
            events.setLocation(eventsRequestUpdateDto.getLocation());
            events.setDate(eventsRequestUpdateDto.getDate());
            events.setTime(eventsRequestUpdateDto.getTime());
            events.setIsFree(eventsRequestUpdateDto.getIsFree());

            Long organizerId = eventsRequestUpdateDto.getOrganizerId();
            if (organizerId != null) {
                User user = userRepository.findById(organizerId)
                .orElseThrow(() -> new RuntimeException("user not found with id " + organizerId));
                events.setOrganizerId(user);
            }

            Long categoriesId = eventsRequestUpdateDto.getCategoryId();
            if (categoriesId != null) {
                Categories categories = categoriesRepository.findById(categoriesId)
                .orElseThrow(() -> new RuntimeException("category not found with id " + categoriesId));
                events.setCategoryId(categories);
            }
            return eventsRepository.save(events);
        } else {
            throw new RuntimeException("Event not found with id " + id);
        }
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
}
