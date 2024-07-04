package com.tickets.ticketmanagement.referrals.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tickets.ticketmanagement.exception.DataNotFoundException;
import com.tickets.ticketmanagement.points.entity.Points;
import com.tickets.ticketmanagement.points.repository.PointsRepository;
import com.tickets.ticketmanagement.referrals.entity.Referrals;
import com.tickets.ticketmanagement.referrals.repository.ReferralsRepository;
import com.tickets.ticketmanagement.referrals.service.ReferralService;
import com.tickets.ticketmanagement.users.entity.User;
import com.tickets.ticketmanagement.users.repository.UserRepository;


@Service
public class ReferralServiceImpl implements ReferralService {

    private final UserRepository userRepository;
    private final ReferralsRepository referralsRepository;
    private final PointsRepository pointsRepository;

    public ReferralServiceImpl (UserRepository userRepository, ReferralsRepository referralsRepository, PointsRepository pointsRepository) {
        this.userRepository = userRepository;
        this.referralsRepository = referralsRepository;
        this.pointsRepository =pointsRepository;
    }

    @Transactional
    @Override
    public void handleNewRegistrationWithReferral(User newUser, String referralCode) {
        User referringUser = userRepository.findByReferralCode(referralCode).orElseThrow(() -> new DataNotFoundException("referring user not found"));
        
        Referrals discount = new Referrals();
        discount.setUser(newUser);
        discount.setReferringUser(referringUser);
        discount.setDiscountAmount(10.0);
        referralsRepository.save(discount);

        Points newPoint = new Points();
        newPoint.setUserId(referringUser);
        newPoint.setPointsBalance(10000);
        pointsRepository.save(newPoint);

    }
}
