package com.tickets.ticketmanagement.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequestDto {

    @NotBlank(message = "Email is required")
    String email;

    @NotBlank(message = "Password is required")
    String password;
}
