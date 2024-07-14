package com.tickets.ticketmanagement.transaction.service.impl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tickets.ticketmanagement.exception.DataNotFoundException;
import com.tickets.ticketmanagement.points.entity.Points;
import com.tickets.ticketmanagement.points.repository.PointsRepository;
import com.tickets.ticketmanagement.referrals.entity.Referrals;
import com.tickets.ticketmanagement.referrals.repository.ReferralsRepository;
import com.tickets.ticketmanagement.tickets.entity.Tickets;
import com.tickets.ticketmanagement.tickets.repository.TicketRepository;
import com.tickets.ticketmanagement.transaction.dto.TicketSelectionDto;
import com.tickets.ticketmanagement.transaction.entity.Transaction;
import com.tickets.ticketmanagement.transaction.repository.TransactionRepository;
import com.tickets.ticketmanagement.transaction.service.TransactionService;
import com.tickets.ticketmanagement.users.entity.User;
import com.tickets.ticketmanagement.users.repository.UserRepository;
@Service
public class TransactionServiceImpl implements TransactionService{

     @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private PointsRepository pointsRepository;

    @Autowired
    private ReferralsRepository referralsRepository;

    @Override
    @Transactional
    // @PreAuthorize("hasRole('PARTICIPANT')")
    public Transaction createTransaction(List<TicketSelectionDto> ticketSelectionDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        User currentUser = userRepository.findByEmail(currentUsername).orElseThrow(() -> new RuntimeException("User not found"));

        Map<Long, Integer> ticketQuentity = new HashMap<>();
        for(TicketSelectionDto selection : ticketSelectionDto) {
            ticketQuentity.put(selection.getTicketId(), selection.getQuantity());
        }

        List<Points> userPoints = pointsRepository.findAllByUserId(currentUser);

        Referrals referral = referralsRepository.findByUserId(currentUser.getId()).orElseThrow(() -> new RuntimeException("Referral not found"));

        double totalAmount = 0;
        double discount = 0;
        for(Map.Entry<Long, Integer> entry : ticketQuentity.entrySet()) {
            Long ticketId = entry.getKey();
            int quantity = entry.getValue();

            Tickets ticket = ticketRepository.findById(ticketId).orElseThrow(() -> new DataNotFoundException("Ticket tier not found"));
            if (!referral.isUsed()) {
                discount += ticket.getPrice() * (referral.getDiscountAmount() / 100) * quantity;
            }
            totalAmount += ticket.getPrice() * quantity;
            ticket.setAvailableSeats(ticket.getAvailableSeats() - quantity);
            ticketRepository.save(ticket);
        }

        double totalPoints = userPoints.stream().mapToDouble(Points::getPointsBalance).sum();
        double finalAmount = totalAmount - discount - totalPoints;

        Points negativPoints = new Points();
        negativPoints.setUserId(currentUser);
        negativPoints.setPointsBalance(-totalPoints);
        negativPoints.setCreatedAt(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
        pointsRepository.save(negativPoints);
        
        if (finalAmount < 0) {
            throw new RuntimeException("Final amount cannot be negative");
        }

        if (!referral.isUsed()) {
            referral.setUsed(true);
            referralsRepository.save(referral);
        }

        Transaction transaction = new Transaction();
        transaction.setParticipant(currentUser);
        transaction.setFinalAmount(finalAmount);
        transaction.setCreatedAt(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
        transactionRepository.save(transaction);

        for(Map.Entry<Long, Integer> entry : ticketQuentity.entrySet()) {
            Long ticketId = entry.getKey();
            int quantity = entry.getValue();

            Tickets ticket = ticketRepository.findById(ticketId).orElseThrow(() -> new DataNotFoundException("ticket tier not found"));

            for(int i=0; i < quantity; i++ ) {
                Transaction ticketTransaction = new Transaction();
                ticketTransaction.setTicketTier(ticket);
                ticketTransaction.setParticipant(currentUser);
                ticketTransaction.setFinalAmount(ticket.getPrice() - (ticket.getPrice() * (referral.getDiscountAmount() / 100)));
                ticketTransaction.setCreatedAt(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
                transactionRepository.save(ticketTransaction);
            }
        }

        return transaction;
    }

}
