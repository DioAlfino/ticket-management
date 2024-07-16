package com.tickets.ticketmanagement.dashboard.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tickets.ticketmanagement.dashboard.dto.SalesDataDto;
import com.tickets.ticketmanagement.dashboard.service.DashboardService;

@RestController
@RequestMapping("/api/v1/dashboard")
public class DashboardController {

    private DashboardService dashboardService;

    public DashboardController (DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/sales/total")
    public SalesDataDto getTotalSalesDataByEventId(
        @RequestParam("eventId") Long eventId) {
            return dashboardService.getTotalSalesDataByEventId(eventId);
    }

    @GetMapping("/sales/daily")
    public List<SalesDataDto> getDailySalesData(
        @RequestParam(value = "eventId", required = false) Long eventId) {
            return dashboardService.getDailySalesDataByEventId(eventId);
    }

    @GetMapping("/sales/weekly")
    public List<SalesDataDto> getWeeklySalesData(@RequestParam("eventId") Long eventId) {
        return dashboardService.getWeeklySalesDataByEventId(eventId);
    }

    @GetMapping("/sales/monthly")
    public List<SalesDataDto> getMonthlySalesData(@RequestParam("eventId") Long eventId) {
        return dashboardService.getmonthlySalesDataByEventId(eventId);
    }

    @GetMapping("/sales/total-sales")
    public ResponseEntity<SalesDataDto> getTotalSalesDataForAllEvents() {
        SalesDataDto totalSalesData = dashboardService.getTotalSalesDataForAllEvents();
        return ResponseEntity.ok(totalSalesData);
    }
}
