package com.tickets.ticketmanagement.points.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tickets.ticketmanagement.auth.helper.Claims;
import com.tickets.ticketmanagement.points.dto.PointsHistoryResponseDto;
import com.tickets.ticketmanagement.points.entity.Points;
import com.tickets.ticketmanagement.points.service.PointsService;

@RestController
@RequestMapping("/api/v1/points")
public class PointsController {

    private final PointsService pointsService;

    public PointsController(PointsService pointsService) {
        this.pointsService = pointsService;
    }

    @GetMapping
    public List<Points> getAllPoints() {
        return pointsService.getAllPoints();
    }

    @GetMapping("/{id}")
    public Optional<Points> getPointsById(@PathVariable Long id) {
        return pointsService.getPointsById(id);
    }

    @DeleteMapping("/{id}")
    public void deletePoints(@PathVariable Long id) {
        pointsService.deletePoints(id);
    }

    @GetMapping("/point")
    public PointsHistoryResponseDto getPoints() {
        var claims = Claims.getClaimsFromJwt();
        var email = (String) claims.get("sub");
        return pointsService.getUsertTotalPoint(email); 
    }
 }
