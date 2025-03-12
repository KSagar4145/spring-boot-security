package com.security.app.config.securityconfig.jwt;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.security.app.config.securityconfig.CustomUserDetailsService;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

	@Autowired
	private JwtUtil jwtUtil;
	@Autowired
	private CustomUserDetailsService userDetailsService;

	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
	        throws ServletException, IOException {
		
		System.err.println("doFilterInternal called");

	    // Retrieve the Authorization header from the request
	    final String authHeader = request.getHeader("Authorization");
	    
	    System.err.println("doFilterInternal authHeader: "+ authHeader);

	    String jwtToken = null;
	    String username = null;

	    // Check if the Authorization header is present and starts with "Bearer "
	    if (authHeader != null && authHeader.startsWith("Bearer ")) {
	        // Extract the JWT token from the Authorization header (excluding "Bearer ")
	        jwtToken = authHeader.substring(7);

	        try {
	            // Extract the username from the token
	            username = jwtUtil.extractUsername(jwtToken);
	        } catch (ExpiredJwtException | SignatureException e) {
	            // Handle cases where the token is expired or invalid
	            System.out.println("Invalid or expired token: " + e.getMessage());
	        }
	    }
	    
	    
	    System.err.println("doFilterInternal username: "+ username);
	    System.err.println("doFilterInternal jwtToken: "+ jwtToken);

	    // If a valid username is extracted and the user is not already authenticated
	    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
	        // Load user details from the database (or other user store)
	        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

	        // Validate the token against the retrieved user details
	        if (jwtUtil.validateToken(jwtToken, userDetails)) {
	            // Create an authentication token for Spring Security
	            UsernamePasswordAuthenticationToken authentication =
	                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

	            // Set additional authentication details (IP address, session info, etc.)
	            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

	            // Store authentication details in the SecurityContext
	            SecurityContextHolder.getContext().setAuthentication(authentication);
	        }
	    }

	    // Continue the filter chain for further processing
	    chain.doFilter(request, response);
	}


}








