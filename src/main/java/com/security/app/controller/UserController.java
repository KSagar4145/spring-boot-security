package com.security.app.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.security.app.entity.User;
import com.security.app.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;

    // Admin: Add a new user
    @PostMapping("/admin/addUser")
    public ResponseEntity<String> addUser(@RequestBody User user) {
        userService.addUser(user);
        return ResponseEntity.ok("User added successfully");
    }

    // Both User & Admin: Get user by email
    @GetMapping("/user/{email}")
    public ResponseEntity<?> getUserByEmail(@PathVariable String email) {
        Optional<User> user = Optional.ofNullable(userService.getUserByEmail(email));
        System.err.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        return user.map(ResponseEntity::ok)
                   .orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    @GetMapping("/admin/getAllUsers")
    public ResponseEntity<?> getAllUsers() {
    	return ResponseEntity.status(HttpStatus.OK).body(userService.getAllUsers());
    }
    
    
    @GetMapping("/user/")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public String start() {
    	return "Welcome to Spring Security only by user";
    }
}



