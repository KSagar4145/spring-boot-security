package com.security.app.config;

import java.util.Optional;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.security.app.entity.User;
import com.security.app.repository.UserRepo;

@Component
public class CustomUserDetailsService implements UserDetailsService{
	
	@Autowired
	private UserRepo userRepo;

//	@Override
//	public UserDetails loadUserByUsername(String userEmail) throws UsernameNotFoundException {
//		
//		Optional<User> user = userRepo.findById(userEmail);
//		
//		return user.map(actualUser  -> new CustomUserDetails(actualUser))
//				.orElseThrow(()->new UsernameNotFoundException("User Not Found"));
//	}
	
	
	
	@Override
	public UserDetails loadUserByUsername(String userEmail) throws UsernameNotFoundException {
	    System.out.println("🔍 Searching for user: " + userEmail);
	    
	    Optional<User> user = userRepo.findById(userEmail);
	    
	    return user.map(actualUser -> {
	        System.out.println("✅ User found: " + actualUser.getEmail());
	        return new CustomUserDetails(actualUser);
	    }).orElseThrow(() -> {
	        System.out.println("❌ User not found: " + userEmail);
	        return new UsernameNotFoundException("User Not Found");
	    });
	}


}
