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

import org.springframework.stereotype.Service;

import com.tickets.ticketmanagement.dashboard.dto.SalesDataDto;
import com.tickets.ticketmanagement.dashboard.repository.DashboardRepository;

@Service
public class DashboardService {

    private final DashboardRepository dashboardRepository;
    
    public DashboardService (DashboardRepository dashboardRepository) {
        this.dashboardRepository = dashboardRepository;
    }

    public SalesDataDto getTotalSalesDataByEventId(Long eventId) {
      SalesDataDto salesData =  dashboardRepository.findTotalSalesDataByEventId(eventId);
        if (salesData == null) {
            return new SalesDataDto((eventId), null, 0L);
        }
        return salesData;
    }

    public List<SalesDataDto> getDailySalesDataByEventId (Long eventId) {
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
