package com.tickets.ticketmanagement.categories.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tickets.ticketmanagement.categories.dto.CategoriesRegisterDto;
import com.tickets.ticketmanagement.categories.entity.Categories;
import com.tickets.ticketmanagement.categories.service.CategoriesService;
import com.tickets.ticketmanagement.response.Response;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoriesController {

    private final CategoriesService categoriesService;

    public CategoriesController(CategoriesService categoriesService) {
        this.categoriesService = categoriesService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> create (@RequestBody CategoriesRegisterDto categoriesRegisterDto) {
        return Response.success("category created successfully", categoriesService.create(categoriesRegisterDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Categories> update(@PathVariable Long id, @RequestBody CategoriesRegisterDto categoriesRegisterDto) {
        try{
            Categories updateCategories = categoriesService.update(id, categoriesRegisterDto);
            return ResponseEntity.ok(updateCategories);
        } catch(RuntimeException e ) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            categoriesService.delete(id);
            return Response.success("User deleted successfully");
        } catch (RuntimeException e) {
            return Response.failed(HttpStatus.NOT_FOUND.value(), "User with ID " + id + " not found");
        }
    }
}
