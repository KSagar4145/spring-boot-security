package com.security.app.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.security.app.entity.User;
import com.security.app.entity.LoginDto;
import com.security.app.service.JwtService;
import com.security.app.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api")
public class UserController {

	@Autowired
	private UserService userService;

	@Autowired
	private JwtService jwtService;
	
	@Autowired
	private AuthenticationManager authenticationManager;

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
	public String start() {
		return "Welcome to Spring Security";
	}


	//JWT
	@PostMapping("/authenticate")
	public String authenticateAndGetToken(@RequestBody LoginDto loginDto) {
		System.err.println("Received authentication request for email: " + loginDto.getEmail());
		
		Authentication authentication = authenticationManager
		.authenticate(
				new UsernamePasswordAuthenticationToken(
						loginDto.getEmail(), loginDto.getPassword()));
		
		if(authentication.isAuthenticated()) {
			String token = jwtService.generateToken(loginDto.getEmail());
			System.err.println("Token sent in response: " + token);
			return token;
		}
		else {
			throw new UsernameNotFoundException("Invalid user request");
		}
		
	}

}



