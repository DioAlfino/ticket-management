package com.tickets.ticketmanagement.transaction.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tickets.ticketmanagement.transaction.dto.TicketSelectionDto;
import com.tickets.ticketmanagement.transaction.entity.Transaction;
import com.tickets.ticketmanagement.transaction.service.TransactionService;


@RestController
@RequestMapping("/api/v1/transaction")
public class TransactionController {

    private TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('PARTICIPANT')")
    public ResponseEntity<?> createTransaction (@RequestBody List<TicketSelectionDto> ticketSelectionDto) {
        try {
            Transaction transaction = transactionService.createTransaction(ticketSelectionDto);
            return ResponseEntity.ok(transaction);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

}
