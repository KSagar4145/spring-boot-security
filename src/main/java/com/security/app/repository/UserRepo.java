package com.security.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.security.app.entity.User;

public interface UserRepo extends JpaRepository<User, String>{

}
