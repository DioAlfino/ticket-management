package com.tickets.ticketmanagement.promotions.dto;

import lombok.Data;

@Data
public class PromotionsDto {
    private Long id;

    private String name;

    private Double discount;
    
    private Integer maxUser;

    public PromotionsDto(String name, Double discount, Integer maxUser, Long id) {
        this.id = id;
        this.name = name;
        this.discount = discount;
        this.maxUser = maxUser;
    }
}
