package com.tickets.ticketmanagement.users.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tickets.ticketmanagement.response.Response;
import com.tickets.ticketmanagement.users.dto.RegisterRequestDto;
import com.tickets.ticketmanagement.users.dto.UserProfileUpdateDto;
import com.tickets.ticketmanagement.users.entity.User;
import com.tickets.ticketmanagement.users.service.UserService;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequestDto registerRequestDto) {
        return Response.success("user registered successfully", userService.register(registerRequestDto));
    }

    @PutMapping("/{id}/profile")
    public ResponseEntity<User> updateProfile(@PathVariable Long id, @RequestBody UserProfileUpdateDto userProfileUpdateDto) {
        try {
            User updateUser = userService.updateProfile(id, userProfileUpdateDto);
            return ResponseEntity.ok(updateUser);
        } catch(RuntimeException e ) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userService.findById(id);
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    
    @GetMapping("/email/{email}")
    public ResponseEntity<Response<User>> getUserByEmail(@PathVariable String email) {
        User user = userService.findByEmail(email);
        if (user != null) {
            return Response.success(HttpStatus.OK.value(), "user fetched successfully", user);
        }  
            return Response.failed(HttpStatus.NOT_FOUND.value(), "user with email " + email + " not found");
    }

   @GetMapping
   public ResponseEntity<?> findAllUser() {
    return Response.success("all usr fatched succcessfully", userService.findAllUser());
   } 

   @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteBy(id);
            return Response.success("User deleted successfully");
        } catch (RuntimeException e) {
            return Response.failed(HttpStatus.NOT_FOUND.value(), "User with ID " + id + " not found");
        }
    }
}
