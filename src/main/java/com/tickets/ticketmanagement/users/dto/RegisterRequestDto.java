package com.tickets.ticketmanagement.users.dto;


import com.tickets.ticketmanagement.users.entity.RolesType;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RegisterRequestDto {

    @NotBlank(message = "name is required")
    private String name;

    @NotBlank(message = "email is required")
    private String email;

    @NotBlank(message = "password is required")
    private String password;

    @NotNull(message = "role is required")
    @Enumerated(EnumType.STRING)
    private RolesType role;

    private String referralCode; 
}
