package com.tickets.ticketmanagement.transaction;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.tickets.ticketmanagement.points.entity.Points;
import com.tickets.ticketmanagement.points.repository.PointsRepository;
import com.tickets.ticketmanagement.referrals.entity.Referrals;
import com.tickets.ticketmanagement.referrals.repository.ReferralsRepository;
import com.tickets.ticketmanagement.tickets.entity.Tickets;
import com.tickets.ticketmanagement.tickets.repository.TicketRepository;
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
    public Transaction createTransaction(Long ticketTierId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        // Mencari user berdasarkan email
        User currentUser = userRepository.findByEmail(currentUsername).orElseThrow(() -> new RuntimeException("User not found"));

        // Memastikan bahwa user memiliki peran yang diizinkan untuk membeli tiket
        if (currentUser.getRole().getId() != 2) {
            throw new RuntimeException("User does not have permission to buy ticket");
        }

        // Mendapatkan informasi tentang ticket tier yang dipilih
        Tickets ticketTier = ticketRepository.findById(ticketTierId).orElseThrow(() -> new RuntimeException("Ticket tier not found"));

        // Mendapatkan semua poin yang dimiliki oleh user
        List<Points> userPoints = pointsRepository.findAllByUserId(currentUser);

        // Mendapatkan referral yang digunakan oleh user
        Referrals referral = referralsRepository.findByUserId(currentUser.getId()).orElseThrow(() -> new RuntimeException("Referral not found"));

        double discount = 0;
        if (!referral.isUsed()) {
            discount = ticketTier.getPrice() * (referral.getDiscountAmount() / 100);
        }

        // Menghitung harga tiket dan diskon
        double ticketPrice = ticketTier.getPrice();
        double totalAmount = ticketPrice - discount;

        double totalPoints = userPoints.stream().mapToDouble(Points::getPointsBalance).sum();
        double finalAmount = totalAmount - totalPoints;

        Points negativPoints = new Points();
        negativPoints.setUserId(currentUser);
        negativPoints.setPointsBalance(-totalPoints);
        negativPoints.setCreatedAt(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
        pointsRepository.save(negativPoints);
        
        if (finalAmount < 0) {
            throw new RuntimeException("Final amount cannot be negative");
        }

        // Mengurangi kursi yang tersedia
        ticketTier.setAvailableSeats(ticketTier.getAvailableSeats() - 1);
        ticketRepository.save(ticketTier);

        if (!referral.isUsed()) {
            referral.setUsed(true);
            referralsRepository.save(referral);
        }

        // Membuat catatan transaksi baru
        Transaction transaction = new Transaction();
        transaction.setTicketTier(ticketTier);
        transaction.setReferral(referral);
        transaction.setParticipant(currentUser);
        transaction.setFinalAmount(finalAmount);
        transaction.setCreatedAt(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
        transactionRepository.save(transaction);

        return transaction;
    }

}
