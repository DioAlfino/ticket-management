package com.tickets.ticketmanagement.auth.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tickets.ticketmanagement.auth.dto.LoginRequestDto;
import com.tickets.ticketmanagement.auth.dto.LoginResponseDto;
import com.tickets.ticketmanagement.auth.entity.UserAuth;
import com.tickets.ticketmanagement.auth.service.AuthService;

import jakarta.servlet.http.Cookie;
import lombok.extern.java.Log;

@Log
@RestController
@RequestMapping("/api/v1/auth")
@Validated
public class AuthController {

    private final AuthService authService;
    private final AuthenticationManager authenticationManager;

    public AuthController (AuthService authService, AuthenticationManager authenticationManager) {
        this.authService = authService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login (@RequestBody LoginRequestDto userLogin) throws IllegalAccessException {

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userLogin.getEmail(), userLogin.getPassword()));

        var ctx = SecurityContextHolder.getContext();
        ctx.setAuthentication(authentication);

        UserAuth userDetails = (UserAuth) authentication.getPrincipal();
        String token = authService.generateToken(authentication);

        LoginResponseDto response = new LoginResponseDto();
        response.setMessage("user logged in successfully!");
        response.setToken(token);

        Cookie cookie = new Cookie("sid", token);
        HttpHeaders headers = new HttpHeaders();
        headers.add("set-cookie", cookie.getName() + "=" + cookie.getValue() + "; Path=/; HttpOnly");
        return ResponseEntity.status(HttpStatus.OK).headers(headers).body(response);
    }
 }
