package com.tickets.ticketmanagement.points.service;

// import java.time.Instant;
// import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.tickets.ticketmanagement.exception.DataNotFoundException;
// import com.tickets.ticketmanagement.points.dao.TotalUserPointsDto;
import com.tickets.ticketmanagement.points.dao.UserPointsBalanceDao;
import com.tickets.ticketmanagement.points.dto.PointsHistoryResponseDto;
import com.tickets.ticketmanagement.points.entity.Points;
import com.tickets.ticketmanagement.points.repository.PointsRepository;
import com.tickets.ticketmanagement.users.entity.User;
import com.tickets.ticketmanagement.users.repository.UserRepository;

@Service
public class PointsService {
    private final UserRepository userRepository;

    private final PointsRepository pointsRepository;

    public PointsService(PointsRepository pointsRepository, UserRepository userRepository) {
        this.pointsRepository = pointsRepository;
        this.userRepository = userRepository;
    }

    public List<Points> getAllPoints() {
        return pointsRepository.findAll();
    }

    public Optional<Points> getPointsById(Long id) {
        return pointsRepository.findById(id);
    }

    public void deletePoints(Long id) {
        pointsRepository.deleteById(id);
    }

    public PointsHistoryResponseDto getUsertTotalPoint (String email) {
        // Instant expiredDate = Instant.now().minus(90, ChronoUnit.DAYS);
        User userData = userRepository.findByEmail(email).orElseThrow(() -> new DataNotFoundException("you are not loggin in yet"));
        UserPointsBalanceDao data = pointsRepository.getTotalPoints(userData.getId());
        PointsHistoryResponseDto points = new PointsHistoryResponseDto();
        if (data == null) {
            points.setPoint(0);
        } else {
            points.setPoint(data.getPoint());
        }
        return points;
        
    }
}
