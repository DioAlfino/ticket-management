package com.tickets.ticketmanagement.referrals.service;

import com.tickets.ticketmanagement.users.entity.User;

public interface ReferralService {
    void handleNewRegistrationWithReferral(User newUser, String referralCode);
}
