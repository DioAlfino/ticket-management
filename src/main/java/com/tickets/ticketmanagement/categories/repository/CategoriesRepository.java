package com.tickets.ticketmanagement.categories.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tickets.ticketmanagement.categories.entity.Categories;

@Repository
public interface CategoriesRepository extends JpaRepository<Categories, Long> {

}
