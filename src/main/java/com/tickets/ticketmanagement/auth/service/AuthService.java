package com.tickets.ticketmanagement.auth.service;

import org.springframework.security.core.Authentication;

import com.tickets.ticketmanagement.auth.dto.LoginResponseDto;

public interface AuthService {
    LoginResponseDto generateToken (Authentication authentication);
}
