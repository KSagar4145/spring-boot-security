package com.security.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.security.app.config.securityconfig.jwt.JwtUtil;
import com.security.app.entity.LoginDto;

@RestController
@RequestMapping("/api")
public class LoginController {
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private JwtUtil jwtUtil;
	
	
	//JWT
		@PostMapping("/login")
		public String authenticateAndGetToken(@RequestBody LoginDto loginDto) {
			System.err.println("Received authentication request for email: " + loginDto.getEmail());
			
			//return  jwtUtil.generateToken(loginDto.getEmail());
			
			Authentication authentication = authenticationManager
			.authenticate(
					new UsernamePasswordAuthenticationToken(
							loginDto.getEmail(), loginDto.getPassword()));
			
			if(authentication.isAuthenticated()) {
				String token = jwtUtil.generateToken(loginDto.getEmail());
				System.err.println("Token sent in response: " + token);
				return token;
			}
			else {
				throw new UsernameNotFoundException("Invalid user request");
			}
			
		}
	

}
