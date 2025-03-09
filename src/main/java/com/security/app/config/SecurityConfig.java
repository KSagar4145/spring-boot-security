package com.security.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)// Enables method-level security
public class SecurityConfig {

	// 1. Define a PasswordEncoder Bean to hash passwords
	@Bean
	 PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	// 2. Define a UserDetailsService Bean to fetch user details from DB
	@Bean
	 UserDetailsService userDetailsService() {
		System.err.println("Authentication userDetailsService!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! ");
		return new CustomUserDetailsService();
	}
	
//	@Bean//Authentication first this will invoke
//	 UserDetailsService userDetailsService(PasswordEncoder encoder) {
//		System.err.println("Authentication userDetailsService!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! ");
//		Used for InMemoryUserDetailsManager
//		UserDetails admin = User
//				.withUsername("admin")
//				.password(encoder.encode("adminpass"))
//				.roles("ADMIN")
//				.build();
//
//		UserDetails user = User
//				.withUsername("user")
//				.password(encoder.encode("userpass"))
//				.roles("USER")
//				.build();
//		
//		UserDetails raman = User
//				.withUsername("Raman")
//				.password(encoder.encode("Raman@123"))
//				.roles("USER")
//				.build();
//		return new InMemoryUserDetailsManager(admin,user, raman);
//	}
	
	
	
	// 3. Define an AuthenticationProvider to validate user credentials
		@Bean
		 AuthenticationProvider authenticationProvider() {
		    DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		    authenticationProvider.setUserDetailsService(userDetailsService());
		    authenticationProvider.setPasswordEncoder(passwordEncoder());
		    return authenticationProvider;
		}
	

	// 4. Define a SecurityFilterChain to manage authentication & authorization
	@Bean
	 SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		System.err.println("Authorization securityFilterChain!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! ");
		 System.err.println("Initializing security configuration...");
		http
		.authorizeHttpRequests(auth-> auth
		        .requestMatchers("/api/authenticate").permitAll()  //bypass authentication for this endpoint
		        .requestMatchers("/api/user/**").hasAnyRole("USER","ADMIN")  // / User APIs accessible to ROLE_USER
	            .requestMatchers("/api/admin/**").hasRole("ADMIN") // Admin APIs accessible to ROLE_ADMIN
	         	//.anyRequest().authenticated()  // this anyRequest().authenticated() will allow all URL once the User and password is given. To avoid this using anyRequest().denyAll()
	            .anyRequest().denyAll()
	            )
		 .httpBasic(Customizer.withDefaults()) // Updated to avoid deprecated method // Enables Basic Authentication
	        .csrf(csrf -> csrf.disable());  // Disable CSRF for testing purposes
		
		 System.err.println("Security configuration setup complete!");
	    return http.build();//SecurityFilterChain is interface
	}
	
	
	@Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
		return authConfig.getAuthenticationManager();
		
	}
	
	
	


}

