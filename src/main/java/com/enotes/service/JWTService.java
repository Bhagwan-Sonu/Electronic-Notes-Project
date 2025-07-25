package com.enotes.service;

import org.springframework.security.core.userdetails.UserDetails;

import com.enotes.entity.User;

public interface JWTService {

	public String generateToken(User user);
	
	public String extractUsername(String token);
	
	public Boolean validateToken(String token, UserDetails userDetails);
}
