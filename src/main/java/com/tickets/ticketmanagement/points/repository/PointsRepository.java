package com.tickets.ticketmanagement.points.repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
// import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.tickets.ticketmanagement.points.dao.UserPointsBalanceDao;
import com.tickets.ticketmanagement.points.entity.Points;
import com.tickets.ticketmanagement.users.entity.User;

@Repository
public interface PointsRepository extends JpaRepository<Points, Long> {
    Optional<Points> findByUserId (User user);
    // List<Points> findAllByUserId(User userId);

    @Query("SELECT p FROM Points p WHERE p.userId = :user AND p.createdAt <= :now AND p.expiredAt >= :now")
    List<Points> findAllActivePointsByUserId(@Param("now") Instant now, @Param("user") User user);

    @Query(value = "select sum (p.pointsBalance) as point from Points p where userId.id = :userId")
    UserPointsBalanceDao getTotalPoints(@Param("userId")Long userId);
    
}
