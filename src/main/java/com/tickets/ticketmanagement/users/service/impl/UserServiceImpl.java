package com.tickets.ticketmanagement.users.service.impl;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.tickets.ticketmanagement.auth.helper.Claims;
import com.tickets.ticketmanagement.exception.DataNotFoundException;
import com.tickets.ticketmanagement.exception.DatabaseOperationException;
import com.tickets.ticketmanagement.exception.UserAlreadyExistException;
import com.tickets.ticketmanagement.users.dto.RegisterRequestDto;
import com.tickets.ticketmanagement.users.dto.UserProfileUpdateDto;
import com.tickets.ticketmanagement.users.entity.User;
import com.tickets.ticketmanagement.users.repository.UserRepository;
import com.tickets.ticketmanagement.users.service.UserService;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User register(RegisterRequestDto registerRequestDto) {
        if (userRepository.findByEmail(registerRequestDto.getEmail()).isPresent()) {
            throw new UserAlreadyExistException("user with email " + registerRequestDto.getEmail() + " already exists");
        }
        User user = new User();
        user.setName(registerRequestDto.getName());
        user.setEmail(registerRequestDto.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequestDto.getPassword()));
        user.setRole(registerRequestDto.getRole());

        try {
            return userRepository.save(user);
        } catch (DataIntegrityViolationException ex) {
            throw new DatabaseOperationException("Failed to register user due to database error.", ex);
        }
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new DataNotFoundException("user with email " + email + " not found"));
    }

    @Override
    public User findCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        return userRepository.findByEmail(currentUsername)
                .orElseThrow(() -> new DataNotFoundException("User with email " + currentUsername + " not found"));
    }

    @Override
    public List<User> findAllUser() {
        return userRepository.findAll();
    }

    @Override
    public Void deleteBy(Long id) {
        if (!userRepository.existsById(id)) {
            throw new DataNotFoundException("user with id " + id + " not found");
        }
        try {
            userRepository.deleteById(id);
        } catch (DataIntegrityViolationException ex ) {
            throw new DatabaseOperationException("Failed to delete user due to database error", ex);
        }
        return null; 
    }

    @Override
    public User updateProfile(UserProfileUpdateDto updateProfileDto) {
        var claims = Claims.getClaimsFromJwt();
        var email = (String) claims.get("sub");
        User user = userRepository.findByEmail(email).orElseThrow(() -> new DataNotFoundException("User not found with id " + email));
        if (updateProfileDto.getName() != null) {
            user.setName(updateProfileDto.getName());
        } 
        user.setPassword(passwordEncoder.encode(updateProfileDto.getPassword()));
        return userRepository.save(user);

    }

    @Override
    public User findByRefferalCode(String refferalCode) {
        return userRepository.findByEmail(refferalCode).orElseThrow(()-> new DataNotFoundException("user with referral id " + refferalCode + "not found"));
    }

}
