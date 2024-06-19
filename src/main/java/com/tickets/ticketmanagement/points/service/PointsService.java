package com.tickets.ticketmanagement.points.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.tickets.ticketmanagement.points.entity.Points;
import com.tickets.ticketmanagement.points.repository.PointsRepository;

@Service
public class PointsService {

    private final PointsRepository repository;

    public PointsService(PointsRepository repository) {
        this.repository = repository;
    }

    public List<Points> getAllPoints() {
        return repository.findAll();
    }

    public Optional<Points> getPointsById(Long id) {
        return repository.findById(id);
    }

    public void deletePoints(Long id) {
        repository.deleteById(id);
    }
}
