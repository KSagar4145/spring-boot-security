package com.security.app.config.securityconfig.jwt;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.stereotype.Component;
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

@Component
public class JwtUtil {

	//	private static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);

	private static final String SECRET_KEY = "cc307e59f5e10a5f53e0a6f0e3d3c9242c704c96299bbfef748e350bb9e6251340940e7f90b5e1f793d303d89648a7b6b8ba7dd1ab8a43cdb1e62e50028973a0209df35b67cfb88c05876ac0bfdb39fbf74cad8357976c1fa80945c3e92cdd8acb41b3a120d402c223174acb0250d1ad3eb799e186503de9fc9366fed4d60ea898405ac775d2872787de7aad09c9d3ddcd7099f55ebcb93d0440ee4489c8f1e57ab43bc7412918e2589f15b630f1dc959c765c4dc19a7490e0f409bfd0438de922fbd8e7354493610c89fdf49503b2c291377e47c40a6e63618cfd31a610051e7749bd7c985b54157b731f82478031c441c593a40d4113911ab19f3d8bdb703a";



	public String generateToken(String email) {
		Map<String, Object> claims = new HashMap<>();
		claims.put("role", "USER"); // Example claim

		return Jwts.builder()
				.setClaims(claims)  // Adding claims
				.setSubject(email)   // Setting subject (email or username)
				.setHeaderParam("typ", "JWT")  // Setting header type
				.setIssuedAt(new Date(System.currentTimeMillis())) // Issued time
				.setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // Expiration (1 hour)
				.signWith(getSignKey(),SignatureAlgorithm.HS256) // Signing with a secure key
				.compact();
	}


	private Key getSignKey() {
		System.err.println("Decoding secret key...");
		byte [] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
		Key key = Keys.hmacShaKeyFor(keyBytes);
		System.err.println("Key generated successfully!");
		System.err.println("Generated Secret Key (Base64) key: " + key);
		return key;	
	}


	//	  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>..here is logic for JwtAuthFilter>>>>>>>>>>>>>


	// ====================== TOKEN EXTRACTION METHODS ======================

	/*
	 * Extracts the username (subject) from the JWT token. 
	 * The subject of the token is set when generating the JWT.
	 */
	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}


	/*
	 * Extracts the expiration date from the JWT token.
	 *  This is used to check if the token is still valid.
	 */
	private Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}


	/*
	 * Extracts a specific claim from the JWT token. Uses a function to apply a
	 * specific claim retrieval logic.
	 */
	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token); // Extracts all claims first
		return claimsResolver.apply(claims); // Applies the given function to retrieve the specific claim
	}

	/*
	 * Extracts all claims from the JWT token.
	 * This is useful when we need multiple claims from the token.
	 */
	private Claims extractAllClaims(String token) {
		return Jwts.parserBuilder()
				.setSigningKey(getSignKey()) // Set the signing key to validate the token
				.build()
				.parseClaimsJws(token) // Parse and validate the JWT
				.getBody(); // Extract the claims (payload)
	}

	// ====================== TOKEN VALIDATION METHODS ======================

	/*
	 * Checks if the JWT token is expired.
	 * Compares the expiration date with the current date.
	 */
	private boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date()); // If expiration date is before now, token is expired
	}

	/*
	 * Validates the JWT token against the provided UserDetails.
	 * Ensures that the username in the token matches the user details and that the token is not expired.
	 */
	public boolean validateToken(String token, UserDetails userDetails) {
		final String username = extractUsername(token); // Get username from token
		return (username.equals(userDetails.getUsername())) && !isTokenExpired(token); // Check username match and expiration
	}



}








/*@Service
public class JwtService {

	//Encryption key 256 from random encryption key generator
	public static final String SECRET = "2B4B6250655375666B5970337336763979244226452948404D635166546A576D";
//			"X2RmZzNvbHRzbzNzZGNmZGZnMTIzNDU2Nzg5MHp4Y2h2Ym5t";

	// Extract username from the JWT token
	public String extractUserEmail(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	 // Extract expiration date from the JWT token
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }


    // Extract a specific claim from the JWT token
	public <T>T extractClaim(String token, Function<Claims, T> claimResolver) {
		final Claims claims = extractAllClaims(token);
		return claimResolver.apply(claims);
	}

	 // Extract all claims (payload) from the token
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
		claims.put("email", email);
		return createToken(claims,email);
	}

	private String createToken(Map<String, Object> claims, String email) {
		System.err.println("Creating token with claims: " + claims);
		String token =  Jwts
				.builder()
				//.setClaims(claims)
				.setSubject(email)
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis()+1000*60*60))
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



	 // Validate JWT token (check username and expiration)
//    public Boolean validateToken(String token, UserDetails userDetails) {
//        final String username = extractUsername(token);
//        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
//    }

}


 */