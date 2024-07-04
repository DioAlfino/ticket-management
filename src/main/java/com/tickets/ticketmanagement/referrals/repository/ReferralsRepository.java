package com.tickets.ticketmanagement.referrals.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tickets.ticketmanagement.referrals.entity.Referrals;

@Repository
public interface ReferralsRepository extends JpaRepository<Referrals, Long>{

    Optional<Referrals> findByUserId(Long userId);
}
