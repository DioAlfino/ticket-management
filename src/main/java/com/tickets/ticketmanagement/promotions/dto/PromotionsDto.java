package com.tickets.ticketmanagement.promotions.dto;

import lombok.Data;

@Data
public class PromotionsDto {

    private String name;

    private Double discount;
    
    private Integer maxUser;

    public PromotionsDto(String name, Double discount, Integer maxUser) {
        this.name = name;
        this.discount = discount;
        this.maxUser = maxUser;
    }
}
