package com.tickets.ticketmanagement.categories.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategoriesRegisterDto {

    @NotBlank(message = "name is required")
    private String name;

    private String description;
}
