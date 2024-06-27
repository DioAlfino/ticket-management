package com.tickets.ticketmanagement.events.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tickets.ticketmanagement.events.entity.Events;

@Repository
public interface EventsRepository extends JpaRepository<Events, Long> {
    Optional<Events> findByName(String name);

    @Query("SELECT e FROM Events e WHERE " + 
        "(:startDate IS NULL OR e.date >= :startDate) AND " +
        "(:endDate IS NULL OR e.date <= :endDate) AND " +
        "(:location IS NULL OR e.location = :location) AND " +
        "(:categoryId IS NULL OR e.categoryId.id = :categoryId) AND " +
        "(:isFree IS NULL OR e.isFree = :isFree)"
    )
    List<Events> filterEvents (
        @Param("startDate") LocalDate startDate,
        @Param ("endDate") LocalDate endDate,
        @Param ("location") String location,
        @Param ("categoryId") Long categoryId,
        @Param ("isFree") Boolean isFree);

}
