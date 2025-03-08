package com.security.app.config;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.security.app.entity.User;

public class CustomUserDetails implements UserDetails {
	
	private String email;
	private String password;
	List<GrantedAuthority> authorities;
	
	// 6. Constructor initializes user details
	public CustomUserDetails(User user) {
		this.email=user.getEmail();
		this.password=user.getPassword();
		this.authorities=Arrays.stream(user.getRole().split(","))
				.map(role->new SimpleGrantedAuthority("ROLE_"+role))// .map(role -> new SimpleGrantedAuthority(role))
				.collect(Collectors.toList());
	}
	

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		return authorities;
	}

	@Override
	public String getPassword() {
		// TODO Auto-generated method stub
		return password;
	}

	@Override
	public String getUsername() {
		// TODO Auto-generated method stub
		return email;
	}
	
	@Override
	public boolean isCredentialsNonExpired() {
		// TODO Auto-generated method stub
		//return UserDetails.super.isCredentialsNonExpired();
		return true;
	}
	
	
	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
//		return UserDetails.super.isEnabled();
		return true;
	}
	
	@Override
	public boolean isAccountNonExpired() {
		// TODO Auto-generated method stub
//		return UserDetails.super.isAccountNonExpired();
		return true;
	}

}
