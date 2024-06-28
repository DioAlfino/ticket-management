package com.tickets.ticketmanagement.auth.service.impl;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.tickets.ticketmanagement.auth.entity.UserAuth;
import com.tickets.ticketmanagement.users.entity.User;
import com.tickets.ticketmanagement.users.repository.UserRepository;

import lombok.extern.java.Log;

@Service
@Log
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User exisingUser = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        var userAuth = new UserAuth();
        log.info(exisingUser.toString());
        userAuth.setEmail(exisingUser.getEmail());
        userAuth.setPassword(exisingUser.getPassword());
        return userAuth;
    }

}
