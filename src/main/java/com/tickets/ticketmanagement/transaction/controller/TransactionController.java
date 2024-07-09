package com.tickets.ticketmanagement.transaction.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tickets.ticketmanagement.transaction.Transaction;
import com.tickets.ticketmanagement.transaction.TransactionService;

@RestController
@RequestMapping("/api/v1/transaction")
public class TransactionController {

    private TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createTransaction (@RequestParam Long ticketTierId) {
        try {
            Transaction transaction = transactionService.createTransaction(ticketTierId);
            return ResponseEntity.ok(transaction);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

}
