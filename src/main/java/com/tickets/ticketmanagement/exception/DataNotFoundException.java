package com.tickets.ticketmanagement.exception;

import java.io.IOException;

public class DataNotFoundException extends RuntimeException {
    public DataNotFoundException(String string) {
        super();
    }

    public DataNotFoundException(String string, IOException e) {
        super();
    }
}