package com.tickets.ticketmanagement.referrals.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.tickets.ticketmanagement.referrals.entity.Referrals;
import com.tickets.ticketmanagement.referrals.repository.ReferralsRepository;

@Service
public class ReferralsService {

    private final ReferralsRepository referralsRepository;

    public ReferralsService(ReferralsRepository referralRepository) {
        this.referralsRepository = referralRepository;
    }
    public List<Referrals> getAllReferrals() {
        return referralsRepository.findAll();
    }

    public Optional<Referrals> getReferralById(Long id) {
        return referralsRepository.findById(id);
    }

    public void deleteReferral(Long id) {
        referralsRepository.deleteById(id);
    }
}
