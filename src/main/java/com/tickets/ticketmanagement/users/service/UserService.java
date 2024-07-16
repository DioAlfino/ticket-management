package com.tickets.ticketmanagement.users.service;

import java.util.List;

import com.tickets.ticketmanagement.users.dto.RegisterRequestDto;
import com.tickets.ticketmanagement.users.dto.UserProfileUpdateDto;
import com.tickets.ticketmanagement.users.entity.User;

public interface UserService {

    User register (RegisterRequestDto user);
    User findByEmail(String email);
    User findByRefferalCode(String refferalCode);
    User findCurrentUser();
    List<User> findAllUser();
    Void deleteBy(Long id);
    
    User updateProfile(UserProfileUpdateDto updateProfileDto);
    
}
