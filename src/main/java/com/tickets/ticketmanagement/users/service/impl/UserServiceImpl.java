package com.tickets.ticketmanagement.users.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
        User user = new User();
        user.setName(registerRequestDto.getName());
        user.setEmail(registerRequestDto.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequestDto.getPassword()));
        RolesEntity roles = new RolesEntity();
        roles.setId(registerRequestDto.getRole());
        user.setRole(roles);
        return userRepository.save(user);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(()-> new RuntimeException("user with id " + id + "not found"));
    }

    @Override
    public List<User> findAllUser() {
        return userRepository.findAll();
    }

    @Override
    public Void deleteBy(Long id) {
        userRepository.deleteById(id);
        return null; 
    }

    @Override
    public User updateProfile(Long id, UserProfileUpdateDto updateProfileDto) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setName(updateProfileDto.getName());
            user.setEmail(updateProfileDto.getEmail());
            user.setPassword(updateProfileDto.getPassword());

            Long roleId = updateProfileDto.getRoleId();
            if (roleId != null) {
                RolesEntity role = rolesRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("role not found with id " + roleId));
                user.setRole(role);
            }
            return userRepository.save(user);
        } else {
            throw new RuntimeException("User not found with id " + id);
        }
    }

    @Override
    public User findByRefferalCode(String refferalCode) {
        return userRepository.findByEmail(refferalCode).orElse(null);
    }

}
