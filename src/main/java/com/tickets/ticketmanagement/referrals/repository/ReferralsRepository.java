package com.tickets.ticketmanagement.referrals.repository;

import java.time.Instant;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tickets.ticketmanagement.referrals.entity.Referrals;

@Repository
public interface ReferralsRepository extends JpaRepository<Referrals, Long>{

    // Optional<Referrals> findByUserId(Long userId);

    @Query("SELECT r FROM Referrals r WHERE r.user.id = :userId AND r.createdAt <= :now AND r.expiredAt >= :now")
    Optional<Referrals> findActiveReferralByUserId(@Param("now") Instant now, @Param("userId") Long userId);
}
