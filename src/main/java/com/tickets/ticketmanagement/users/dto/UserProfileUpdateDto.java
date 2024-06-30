package com.tickets.ticketmanagement.users.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserProfileUpdateDto {
    
    @NotBlank(message = "name is required")
    private String name;

    // @NotBlank(message = "email is required")
    // private String email;
    
    @NotBlank(message = "password is required")
    private String password;

    // private Long roleId;

}
