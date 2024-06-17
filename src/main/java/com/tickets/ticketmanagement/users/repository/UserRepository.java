package com.tickets.ticketmanagement.users.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tickets.ticketmanagement.users.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
}
    