package com.tickets.ticketmanagement.referrals.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tickets.ticketmanagement.referrals.entity.Referrals;
import com.tickets.ticketmanagement.referrals.service.ReferralsService;

@RestController
@RequestMapping("/api/v1/referrals")
public class ReferralsController {

    private final ReferralsService referralsService;

    public ReferralsController(ReferralsService referralsService) {
        this.referralsService = referralsService;
    }

    @GetMapping
    public List<Referrals> getAllReferrals() {
        return referralsService.getAllReferrals();
    }

    @GetMapping("/{id}")
    public Optional<Referrals> getReferralById(@PathVariable Long id) {
        return referralsService.getReferralById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteReferral(@PathVariable Long id) {
        referralsService.deleteReferral(id);
    }
}
