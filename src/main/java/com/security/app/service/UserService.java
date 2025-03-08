package com.security.app.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.security.app.entity.User;
import com.security.app.repository.UserRepo;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class UserService {
	
	@Autowired
    private PasswordEncoder passwordEncoder;
	
	@Autowired
	private UserRepo userRepo;

	public UserService(){
	}
	
	
	public User getUserByEmail(String email) {
		System.err.println("email: "+email);
		return userRepo.findById(email).orElse(null);
	}
	
	
	public User addUser(User user) {
		System.out.println("🔑 Raw Password: " + user.getPassword()); // Debugging
	    String hashedPassword = passwordEncoder.encode(user.getPassword());
	    System.out.println("🔐 Hashed Password: " + hashedPassword); // Debugging
	    user.setPassword(hashedPassword);
	    return userRepo.save(user);
	}


	public List<User> getAllUsers() {
		return userRepo.findAll();
	}

}




//For InMemory
/*
@Service
public class UserService {
	
	@Autowired
    private PasswordEncoder passwordEncoder;

	ArrayList<User> users = new ArrayList<>();
	public UserService(){

        // Adding default users (for testing)
        users.add(new User("admin", "admin@example.com", "adminpass", "ADMIN"));
        users.add(new User("user", "user@example.com", "userpass", "USER"));
	}
	
	
	public User getUserByEmail(String email) {
		System.err.println("email: "+email);
		return this.users.stream()
				.filter(user->user.getEmail().equals(email))
				 .peek(user -> user.setPassword(passwordEncoder.encode(user.getPassword()))) // Encode the password
				.findAny().orElse(null);
	}
	
	
	public User addUser(User user) {
		String pass= user.getPassword();
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		this.users.add(user);
		System.err.println(this.users);
		return user;
	}


	public List<User> getAllUsers() {
		// TODO Auto-generated method stub
		return this.users;
	}

}
*/