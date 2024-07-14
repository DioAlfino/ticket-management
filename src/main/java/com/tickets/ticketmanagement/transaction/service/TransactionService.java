package com.tickets.ticketmanagement.transaction.service;

import java.util.List;

import com.tickets.ticketmanagement.transaction.dto.TicketSelectionDto;
import com.tickets.ticketmanagement.transaction.entity.Transaction;

public interface TransactionService {
    Transaction createTransaction(List<TicketSelectionDto> ticketSelectionsd);
}
