package com.tickets.ticketmanagement.categories.service;

import com.tickets.ticketmanagement.categories.dto.CategoriesRegisterDto;
import com.tickets.ticketmanagement.categories.entity.Categories;

public interface CategoriesService {

    Categories create (CategoriesRegisterDto categorie);
    Categories update(Long id, CategoriesRegisterDto categorie);
    Categories delete(Long id);
}
