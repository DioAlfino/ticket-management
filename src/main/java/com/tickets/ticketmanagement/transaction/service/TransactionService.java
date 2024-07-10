package com.tickets.ticketmanagement.transaction.service;

import com.tickets.ticketmanagement.transaction.entity.Transaction;

public interface TransactionService {
    Transaction createTransaction(Long ticketTierId);
}
