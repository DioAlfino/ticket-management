package com.tickets.ticketmanagement.dashboard.repository;

import java.time.Instant;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tickets.ticketmanagement.dashboard.dto.SalesDataDto;
import com.tickets.ticketmanagement.transaction.entity.Transaction;

@Repository
public interface DashboardRepository extends JpaRepository<Transaction, Long> {

    @Query("SELECT new com.tickets.ticketmanagement.dashboard.dto.SalesDataDto(t.ticketTier.event.id, CAST(t.createdAt AS LocalDate), COUNT(t.id)) " +
           "FROM Transaction t " +
           "WHERE t.ticketTier.event.id = :eventId " +
           "GROUP BY t.ticketTier.event.id, CAST(t.createdAt AS LocalDate)")
    List<SalesDataDto> findSalesDataByEventId(@Param("eventId") Long eventId);

    @Query("SELECT new com.tickets.ticketmanagement.dashboard.dto.SalesDataDto(t.ticketTier.event.id, null, COUNT(t.id)) " +
        "FROM Transaction t " +
        "WHERE t.ticketTier.event.id = :eventId " +
        "GROUP BY t.ticketTier.event.id")
    SalesDataDto findTotalSalesDataByEventId(@Param("eventId") Long eventId);

    @Query("SELECT new com.tickets.ticketmanagement.dashboard.dto.SalesDataDto(t.ticketTier.event.id, CAST(t.createdAt AS LocalDate), COUNT(t.id)) " +
        "FROM Transaction t " +
        "WHERE t.ticketTier.event.id = :eventId AND t.createdAt >= :startDate AND t.createdAt <= :endDate " +
        "GROUP BY t.ticketTier.event.id, CAST(t.createdAt AS LocalDate)")
    List<SalesDataDto> findDailySalesDataByEventId(
        @Param("eventId") Long eventId,
        @Param("startDate") Instant startDate,
        @Param("endDate") Instant endDate);

    @Query("SELECT new com.tickets.ticketmanagement.dashboard.dto.SalesDataDto(t.ticketTier.event.id, CAST(t.createdAt AS LocalDate), COUNT(t.id)) " +
        "FROM Transaction t " +
        "WHERE t.ticketTier.event.id = :eventId AND t.createdAt >= :startDate AND t.createdAt <= :endDate " +
        "GROUP BY t.ticketTier.event.id, CAST(t.createdAt AS LocalDate)")
    List<SalesDataDto> findWeeklySalesDataByEventId(
        @Param("eventId") Long eventId,
        @Param("startDate") Instant startDate,
        @Param("endDate") Instant endDate);

    @Query("SELECT new com.tickets.ticketmanagement.dashboard.dto.SalesDataDto(t.ticketTier.event.id, CAST(t.createdAt AS LocalDate), COUNT(t.id)) " +
        "FROM Transaction t " +
        "WHERE t.ticketTier.event.id = :eventId AND t.createdAt >= :startDate AND t.createdAt <= :endDate " +
        "GROUP BY t.ticketTier.event.id, CAST(t.createdAt AS LocalDate)")
    List<SalesDataDto> findMonthlySalesDataByEventId(
        @Param("eventId") Long eventId,
        @Param("startDate") Instant startDate,
        @Param("endDate") Instant endDate);
}
