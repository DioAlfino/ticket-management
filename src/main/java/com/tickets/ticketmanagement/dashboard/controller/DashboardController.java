package com.tickets.ticketmanagement.dashboard.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tickets.ticketmanagement.dashboard.dto.SalesDataDto;
import com.tickets.ticketmanagement.dashboard.service.DashboardService;
import com.tickets.ticketmanagement.response.Response;

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
    public ResponseEntity<List<SalesDataDto>> getWeeklySalesData() {
        List<SalesDataDto> weeklySalesData = dashboardService.getWeeklySalesDataByOrganizerId(null);
        return ResponseEntity.ok(weeklySalesData);
    }
    
    @GetMapping("/sales/monthly")
    public ResponseEntity<List<SalesDataDto>> getMonthlySalesData() {
        List<SalesDataDto> monthlySalesData = dashboardService.getmonthlySalesDataByEventId(null);
        return ResponseEntity.ok(monthlySalesData);
    }

    @GetMapping("/sales/total-sales")
    public ResponseEntity<Response<List<SalesDataDto>>> getTotalSalesDataForAllEvents() {
        List<SalesDataDto> totalSalesData = dashboardService.getTotalSalesDataForAllEvents();
        return Response.success("succss", totalSalesData);
    }
    @GetMapping("/sales/total-revenue")
    public ResponseEntity<Response<Long>> getTotalRevenueForAllEvents() {
        Long totalRevenue = dashboardService.getTotalRevenueForAllEvents();
        return Response.success("success", totalRevenue);
    }

    @GetMapping("/events/total")
    public ResponseEntity<Long> getTotalEventsByCurrentUser() {
        Long totalEvents = dashboardService.getTotalEventsByCurrentUser();
        return ResponseEntity.ok(totalEvents);
    }
}
