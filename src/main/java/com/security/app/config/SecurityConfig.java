package com.security.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
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
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

	@Bean
	 PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean//Authentication first this will invoke
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
	

	@Bean//Authorization Second this will invoke
	 SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		System.err.println("Authorization securityFilterChain!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! ");
		http
		.authorizeHttpRequests(auth-> auth
		        .requestMatchers("/api/user/**").hasRole("USER")  // Only Admin can add users
	            .requestMatchers("/api/admin/**").hasAnyRole("ADMIN") // Both can get users
	         	//.anyRequest().authenticated()  // this anyRequest().authenticated() will allow all URL once the User and password is given. To avoid this using anyRequest().denyAll()
	            .anyRequest().denyAll()
	            )
		 .httpBasic(Customizer.withDefaults()) // Updated to avoid deprecated method
	        .csrf(csrf -> csrf.disable());  // Disable CSRF for testing purposes
		
		
	    return http.build();//SecurityFilterChain is interface
	}
	
	
	@Bean
	 AuthenticationProvider authenticationProvider() {
	    DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
	    authenticationProvider.setUserDetailsService(userDetailsService());
	    authenticationProvider.setPasswordEncoder(passwordEncoder());
	    return authenticationProvider;
	}


}

