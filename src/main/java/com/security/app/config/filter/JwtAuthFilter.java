package com.security.app.config.filter;

import java.io.IOException;

import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtAuthFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		String token =null;
		String authHeader = request.getHeader("Authorization");
				
				if(authHeader!=null && authHeader.startsWith("Bearer ")) {
					token= authHeader.substring(7);
					
				}
		
	}

}
