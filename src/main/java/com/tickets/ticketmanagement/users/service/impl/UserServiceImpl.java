package com.tickets.ticketmanagement.users.service.impl;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.tickets.ticketmanagement.exception.DataNotFoundException;
import com.tickets.ticketmanagement.exception.DatabaseOperationException;
import com.tickets.ticketmanagement.exception.UserAlreadyExistException;
import com.tickets.ticketmanagement.roles.entity.RolesEntity;
import com.tickets.ticketmanagement.roles.repository.RolesRepository;
import com.tickets.ticketmanagement.users.dto.RegisterRequestDto;
import com.tickets.ticketmanagement.users.dto.UserProfileUpdateDto;
import com.tickets.ticketmanagement.users.entity.User;
import com.tickets.ticketmanagement.users.repository.UserRepository;
import com.tickets.ticketmanagement.users.service.UserService;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RolesRepository rolesRepository;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, RolesRepository rolesRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.rolesRepository = rolesRepository;
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
        RolesEntity roles = new RolesEntity();
        roles.setId(registerRequestDto.getRole());
        user.setRole(roles);

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
    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(()-> new DataNotFoundException("user with id " + id + "not found"));
    }

    @Override
    public List<User> findAllUser() {
        return userRepository.findAll();
    }

    @Override
    public Void deleteBy(Long id) {
        if (userRepository.existsById(id)) {
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
    public User updateProfile(Long id, UserProfileUpdateDto updateProfileDto) {
        User user = userRepository.findById(id).orElseThrow(() -> new DataNotFoundException("User not found with id " + id));
            user.setName(updateProfileDto.getName());
            user.setEmail(updateProfileDto.getEmail());
            user.setPassword(updateProfileDto.getPassword());

            Long roleId = updateProfileDto.getRoleId();
            if (roleId != null) {
                RolesEntity role = rolesRepository.findById(roleId)
                .orElseThrow(() -> new DataNotFoundException("role not found with id " + roleId));
                user.setRole(role);
            }
            try {
                return userRepository.save(user);
            } catch (DataIntegrityViolationException ex) {
                throw new DatabaseOperationException("Failed to update user profile due to database error.", ex);
            }
    }

    @Override
    public User findByRefferalCode(String refferalCode) {
        return userRepository.findByEmail(refferalCode).orElseThrow(()-> new DataNotFoundException("user with referral id " + refferalCode + "not found"));
    }

}
