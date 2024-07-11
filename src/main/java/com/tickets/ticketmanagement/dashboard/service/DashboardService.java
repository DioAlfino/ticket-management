package com.tickets.ticketmanagement.dashboard.service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.tickets.ticketmanagement.dashboard.dto.SalesDataDto;
import com.tickets.ticketmanagement.dashboard.repository.DashboardRepository;
import com.tickets.ticketmanagement.events.entity.Events;
import com.tickets.ticketmanagement.events.repository.EventsRepository;
import com.tickets.ticketmanagement.exception.DataNotFoundException;
import com.tickets.ticketmanagement.users.entity.User;
import com.tickets.ticketmanagement.users.repository.UserRepository;

@Service
public class DashboardService {

    private final DashboardRepository dashboardRepository;
    private final UserRepository userRepository;
    private final EventsRepository eventsRepository;
    
    public DashboardService (DashboardRepository dashboardRepository, UserRepository userRepository, EventsRepository eventsRepository) {
        this.dashboardRepository = dashboardRepository;
        this.userRepository = userRepository;
        this.eventsRepository = eventsRepository;
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        return userRepository.findByEmail(currentUsername).orElseThrow(() -> new DataNotFoundException("User not found"));
    }

    private void checkEventOwner(Long eventId) {
        User currentUser = getCurrentUser();
        Events events = eventsRepository.findById(eventId).orElseThrow(() -> new DataNotFoundException("Event not found"));
        if (!events.getOrganizerId().equals(currentUser)) {
            throw new RuntimeException("User does not have accesst to this event");
        }
    }

    public SalesDataDto getTotalSalesDataByEventId(Long eventId) {
        checkEventOwner(eventId);
        SalesDataDto salesData =  dashboardRepository.findTotalSalesDataByEventId(eventId);
        if (salesData == null) {
            return new SalesDataDto((eventId), null, 0L);
        }
        return salesData;
    }

    public List<SalesDataDto> getDailySalesDataByEventId (Long eventId) {
        checkEventOwner(eventId);
        Instant endDate = Instant.now();
        Instant startDate = endDate.minus(24, ChronoUnit.HOURS);
        List<SalesDataDto> saleData = dashboardRepository.findDailySalesDataByEventId(eventId, startDate, endDate);

        Map<LocalDate, SalesDataDto> salesDataMap = saleData.stream().collect(Collectors.toMap(SalesDataDto::getDate, Function.identity()));

        List<SalesDataDto> completeSalesData = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            LocalDate date = startDate.plus(i, ChronoUnit.DAYS).atZone(ZoneId.systemDefault()).toLocalDate();
            completeSalesData.add(salesDataMap.getOrDefault(date, new SalesDataDto(eventId, date, 0L)));
        }
        return completeSalesData;
    }

    public List<SalesDataDto> getWeeklySalesDataByEventId(Long eventId) {
        checkEventOwner(eventId);
        Instant endDate = Instant.now();
        Instant startDate = endDate.minus(7, ChronoUnit.DAYS);
        List<SalesDataDto> salesData = dashboardRepository.findWeeklySalesDataByEventId(eventId, startDate, endDate);

        // Mengisi tanggal yang kosong dengan salesCount 0
        Map<LocalDate, SalesDataDto> salesDataMap = salesData.stream()
            .collect(Collectors.toMap(SalesDataDto::getDate, Function.identity()));

        List<SalesDataDto> completeSalesData = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            LocalDate date = startDate.plus(i, ChronoUnit.DAYS).atZone(ZoneId.systemDefault()).toLocalDate();
            completeSalesData.add(salesDataMap.getOrDefault(date, new SalesDataDto(eventId, date, 0L)));
        }
        return completeSalesData;
    }
    public List<SalesDataDto> getmonthlySalesDataByEventId(Long eventId) {
    Instant endDate = Instant.now();
    Instant startDate = endDate.minus(30, ChronoUnit.DAYS);
    List<SalesDataDto> salesData = dashboardRepository.findWeeklySalesDataByEventId(eventId, startDate, endDate);

    Map<LocalDate, SalesDataDto> salesDataMap = salesData.stream()
        .collect(Collectors.toMap(SalesDataDto::getDate, Function.identity()));

    List<SalesDataDto> completeSalesData = new ArrayList<>();
    for (int i = 0; i < 30; i++) {
        LocalDate date = startDate.plus(i, ChronoUnit.DAYS).atZone(ZoneId.systemDefault()).toLocalDate();
        completeSalesData.add(salesDataMap.getOrDefault(date, new SalesDataDto(eventId, date, 0L)));
    }

        return completeSalesData;
    }

    
}
