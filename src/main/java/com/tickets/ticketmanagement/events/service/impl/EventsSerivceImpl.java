package com.tickets.ticketmanagement.events.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.tickets.ticketmanagement.categories.entity.Categories;
import com.tickets.ticketmanagement.categories.repository.CategoriesRepository;
import com.tickets.ticketmanagement.events.dto.EventsRequestRegisterDto;
import com.tickets.ticketmanagement.events.dto.EventsRequestUpdateDto;
import com.tickets.ticketmanagement.events.entity.Events;
import com.tickets.ticketmanagement.events.repository.EventsRepository;
import com.tickets.ticketmanagement.events.service.EventsService;
import com.tickets.ticketmanagement.users.entity.User;
import com.tickets.ticketmanagement.users.repository.UserRepository;

@Service
public class EventsSerivceImpl implements EventsService {

    private final EventsRepository eventsRepository;
    private final UserRepository userRepository;
    private final CategoriesRepository categoriesRepository;

    public EventsSerivceImpl(EventsRepository eventsRepository, UserRepository userRepository, CategoriesRepository categoriesRepository) {
        this.eventsRepository = eventsRepository;
        this.userRepository = userRepository;
        this.categoriesRepository = categoriesRepository;

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

        User user = new User();
        user.setId(registerDto.getOrganizerId());

        Categories categories = new Categories();
        categories.setId(registerDto.getCategoryId());

        events.setOrganizerId(user);
        events.setCategoriesId(categories);

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
                events.setCategoriesId(categories);
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
    public List<Events> filterByDate(LocalDate starDate, LocalDate endDate) {
        return eventsRepository.findByDateBetween(starDate, endDate);
    }

    @Override
    public List<Events> filterByLocation(String location) {
        return eventsRepository.findByLocation(location);
    }

    @Override
    public List<Events> filterByCategory(Categories category) {
        return eventsRepository.findByCageroiesId(category);
    }

    @Override
    public List<Events> filterByIsFree(Boolean isFree) {
        return eventsRepository.findByIsFree(isFree);
    }

   

}
