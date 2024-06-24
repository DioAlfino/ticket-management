package com.tickets.ticketmanagement.events.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tickets.ticketmanagement.categories.entity.Categories;
import com.tickets.ticketmanagement.events.entity.Events;

@Repository
public interface EventsRepository extends JpaRepository<Events, Long> {
    Optional<Events> findByName(String name);
    List<Events> findByDateBetween(LocalDate startDate, LocalDate endDate);
    List<Events> findByLocation(String location);
    List<Events> findByCageroiesId (Categories category);

    @Query("SELECT e FROM Events e WHERE e.isFree = :isFree")
    List<Events> findByIsFree (@Param ("isFree") Boolean isFree);

}
