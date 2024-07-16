package com.tickets.ticketmanagement.events.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tickets.ticketmanagement.events.entity.Events;

@Repository
public interface EventsRepository extends JpaRepository<Events, Long> {
    List<Events> findByNameContainingIgnoreCase(String name);

    @Query("SELECT COUNT(e) FROM Events e WHERE e.organizerId.id = :organizerId")
    Long countEventsByOrganizerId(@Param("organizerId") Long organizerId);


    @Query("SELECT e FROM Events e WHERE " + 
       "(:location IS NULL OR e.location = :location) AND " +
       "(:categoryId IS NULL OR e.categoryId.id = :categoryId) AND " +
       "(:isFree IS NULL OR e.isFree = :isFree) AND " +
       "(:name IS NULL OR LOWER(e.name) LIKE %:name%)"
       )
Page<Events> filterEvents(
        @Param("location") String location,
        @Param("categoryId") Long categoryId,
        @Param("isFree") Boolean isFree,
        @Param("name") String name,
        Pageable pageable);


}
