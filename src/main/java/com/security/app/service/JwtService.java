package com.security.app.service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoder;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;

/*
 * JWT is used in authentication (Spring Security), API security, and OAuth implementations.
 * 
 * JWT (JSON Web Token) consists of three parts, separated by dots (.): header.payload.signature
 * 
 * 1.-->HeaderThe header contains metadata about the token, including the type (JWT) and signing algorithm used.
 * 2.-->The payload contains the actual claims (data) of the token, such as user ID, roles, and expiration time.
 * 3.-->The signature ensures that the token was not tampered with. It is created by encrypting the header + payload using the secret key.

	If someone modifies the payload, the signature won't match, and the token will be rejected.

	Part		Purpose										Example
1.  Header		Defines JWT type & signing algorithm		{"alg": "HS256", "typ": "JWT"}
2.	Payload		Contains user info & claims					{"sub": "1234567890", "role": "ADMIN"}
3.	Signature	Ensures integrity & prevents tampering		HMACSHA256(header.payload, secretKey)
 * 
 * 
 */


@Service
public class JwtService {

	//Encryption key 256 from random encryption key generator
	public static final String SECRET = "2B4B6250655375666B5970337336763979244226452948404D635166546A576D";
//			"X2RmZzNvbHRzbzNzZGNmZGZnMTIzNDU2Nzg5MHp4Y2h2Ym5t";
	
	public String extractUserEmail(String token) {
		return extractCliam(token, Claims::getSubject);
	}

	private <T>T extractCliam(String token, Function<Claims, T> claimResolver) {
		final Claims claims = extractAllClaims(token);
		return claimResolver.apply(claims);
	}

	private Claims extractAllClaims(String token) {
	    return Jwts
	        .parserBuilder()  // Create a JWT parser
	        .setSigningKey(getSignKey())  // Set the secret key to verify signature
	        .build()  // Build the parser
	        .parseClaimsJws(token)  // Parse the JWT
	        .getBody();  // Extract the claims (payload)
	}
	
	

	public String generateToken(String email) {
		System.err.println("Generating token for email: " + email);
		Map<String,Object> claims = new HashMap<>();
		//claims.put("email", email);
		return createToken(claims,email);
	}

	private String createToken(Map<String, Object> claims, String email) {
		System.err.println("Creating token with claims: " + claims);

		String token =  Jwts
				.builder()
				.setClaims(claims)
				.setSubject(email)
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis()+1000*60*30))
				.signWith(getSignKey(),SignatureAlgorithm.HS256)
				.compact();

		System.err.println("Generated Token: " + token);
		return token;
	}

	private Key getSignKey() {
		System.err.println("Decoding secret key...");
		byte [] keyBytes = Decoders.BASE64.decode(SECRET);
		Key key = Keys.hmacShaKeyFor(keyBytes);
		System.err.println("Key generated successfully!");
		return key;	
	}
	
}
