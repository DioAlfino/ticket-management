package com.tickets.ticketmanagement.transaction.service.impl;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tickets.ticketmanagement.exception.DataNotFoundException;
import com.tickets.ticketmanagement.points.entity.Points;
import com.tickets.ticketmanagement.points.repository.PointsRepository;
import com.tickets.ticketmanagement.promotions.entity.Promotions;
import com.tickets.ticketmanagement.promotions.repository.PromotionsRepository;
import com.tickets.ticketmanagement.referrals.entity.Referrals;
import com.tickets.ticketmanagement.referrals.repository.ReferralsRepository;
import com.tickets.ticketmanagement.tickets.entity.Tickets;
import com.tickets.ticketmanagement.tickets.repository.TicketRepository;
import com.tickets.ticketmanagement.transaction.dto.TicketSelectionDto;
import com.tickets.ticketmanagement.transaction.entity.Transaction;
import com.tickets.ticketmanagement.transaction.entity.TransactionItem;
import com.tickets.ticketmanagement.transaction.repository.TransactionItemRepository;
import com.tickets.ticketmanagement.transaction.repository.TransactionRepository;
import com.tickets.ticketmanagement.transaction.service.TransactionService;
import com.tickets.ticketmanagement.users.entity.User;
import com.tickets.ticketmanagement.users.repository.UserRepository;
@Service
public class TransactionServiceImpl implements TransactionService{

     @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private TransactionItemRepository transactionItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private PointsRepository pointsRepository;

    @Autowired
    private ReferralsRepository referralsRepository;
    @Autowired
    private PromotionsRepository promotionsRepository;


@Override
@Transactional
public Transaction createTransaction(List<TicketSelectionDto> ticketSelectionDto) {
    if (ticketSelectionDto == null || ticketSelectionDto.isEmpty()) {
        throw new IllegalArgumentException("No tickets selected");
    }

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUsername = authentication.getName();

    User currentUser = userRepository.findByEmail(currentUsername)
            .orElseThrow(() -> new DataNotFoundException("User not found"));

    Long eventId = ticketRepository.findById(ticketSelectionDto.get(0).getTicketId())
            .orElseThrow(() -> new DataNotFoundException("Ticket not found"))
            .getEvent().getId();

    Map<Long, Integer> ticketQuantity = new HashMap<>();
    double totalAmount = 0;
    double discount = 0;

    // Process all tickets
    for (TicketSelectionDto selection : ticketSelectionDto) {
        Tickets ticket = ticketRepository.findById(selection.getTicketId())
                .orElseThrow(() -> new DataNotFoundException("Ticket tier not found"));

        if (!ticket.getEvent().getId().equals(eventId)) {
            throw new IllegalArgumentException("All tickets must be from the same event");
        }

        int quantity = selection.getQuantity();
        ticketQuantity.put(selection.getTicketId(), quantity);

        totalAmount += ticket.getPrice() * quantity;
        ticket.setAvailableSeats(ticket.getAvailableSeats() - quantity);
        ticketRepository.save(ticket);
    }

    Instant now = Instant.now();

    List<Points> userPoints = pointsRepository.findAllActivePointsByUserId(now, currentUser);
    Referrals referral = referralsRepository.findActiveReferralByUserId(now, currentUser.getId())
            .orElseThrow(() -> new DataNotFoundException("Referral not found"));

    // Apply referral discount if not used
    if (!referral.isUsed()) {
        discount = totalAmount * (referral.getDiscountAmount() / 100);
    }

    // Apply promotion discount if available
    Promotions activePromotion = promotionsRepository.findActivePromotionForEvent(now, eventId);
    double promotionDiscount = 0;
    if (activePromotion != null) {
        promotionDiscount = activePromotion.getDiscount();
        discount += promotionDiscount;
        
        activePromotion.setMaxUser(activePromotion.getMaxUser() - 1);
        promotionsRepository.save(activePromotion);
        
        System.out.println("Applied promotion: " + activePromotion.getId() + ", Discount: " + promotionDiscount);
    } else {
        System.out.println("No active promotion found for event: " + eventId);
    }

    double totalPoints = userPoints.stream().mapToDouble(Points::getPointsBalance).sum();
    double remainingPoint = totalAmount - discount;
    double pointsToUse = Math.min(remainingPoint, totalPoints);
    double finalAmount = remainingPoint - pointsToUse;

    // Create negative points entry
    if (pointsToUse > 0) {
        Points negativePoints = new Points();
        negativePoints.setUserId(currentUser);
        negativePoints.setPointsBalance(-pointsToUse);
        negativePoints.setCreatedAt(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
        pointsRepository.save(negativePoints);
    }

    if (finalAmount < 0) {
        finalAmount = 0;
    }

    // Mark referral as used
    if (!referral.isUsed()) {
        referral.setUsed(true);
        referralsRepository.save(referral);
    }

    // Create and save transaction
    Transaction transaction = new Transaction();
    transaction.setParticipant(currentUser);
    transaction.setDiscount(discount);
    transaction.setTotalAmount(totalAmount);
    transaction.setPointsUsed(pointsToUse);
    transaction.setFinalAmount(finalAmount);
    transaction.setCreatedAt(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
    transactionRepository.save(transaction);

    // Create and save transaction items
    for (Map.Entry<Long, Integer> entry : ticketQuantity.entrySet()) {
        Long ticketId = entry.getKey();
        int quantity = entry.getValue();

        Tickets ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new DataNotFoundException("Ticket tier not found"));

        TransactionItem transactionItem = new TransactionItem();
        transactionItem.setTransaction(transaction);
        transactionItem.setTickets(ticket);
        transactionItem.setQuantity(quantity);
        transactionItem.setPrice(ticket.getPrice());
        transactionItemRepository.save(transactionItem);
    }

    System.out.println("Transaction created: " + transaction.getId());
    System.out.println("Total amount: " + totalAmount);
    System.out.println("Discount applied: " + discount);
    System.out.println("Points used: " + totalPoints);
    System.out.println("Final amount: " + finalAmount);

    return transaction;
}

}
