package com.tickets.ticketmanagement.categories.service.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.tickets.ticketmanagement.categories.dto.CategoriesRegisterDto;
import com.tickets.ticketmanagement.categories.entity.Categories;
import com.tickets.ticketmanagement.categories.repository.CategoriesRepository;
import com.tickets.ticketmanagement.categories.service.CategoriesService;

@Service
public class CategoriesServiceImpl implements CategoriesService {

    private final CategoriesRepository categoriesRepository;

    public CategoriesServiceImpl(CategoriesRepository categoriesRepository) {
        this.categoriesRepository = categoriesRepository;
    }

    @Override
    public Categories create(CategoriesRegisterDto categorie) {
        Categories categories = new Categories();
        categories.setName(categorie.getName());
        categories.setDescription(categorie.getDescription());
        return categoriesRepository.save(categories);
    }

    @Override
    public Categories update(Long id, CategoriesRegisterDto categorie) {
        Optional<Categories> optional = categoriesRepository.findById(id);
        if (optional.isPresent()) {
            Categories categories = optional.get();
            categorie.setName(categorie.getName());
            categorie.setDescription(categorie.getDescription());
            
            return categoriesRepository.save(categories);
        } else {
            throw new RuntimeException("category not found with id " + id);
        }
    }

    @Override
    public Categories delete(Long id) {
        categoriesRepository.deleteById(id);
        return null;
    }

}
