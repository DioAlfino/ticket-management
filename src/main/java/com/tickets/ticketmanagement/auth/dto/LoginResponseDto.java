package com.tickets.ticketmanagement.auth.dto;

import lombok.Data;

@Data
public class LoginResponseDto {

    private String message;
    private String token;
    private String role;
}
