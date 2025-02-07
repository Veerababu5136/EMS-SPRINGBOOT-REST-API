package com.restapi.eventManagementSystem.security;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/*
 
 Create JwtTokenProvider class it takes arg as authentication interface where it holds username() in this we get email from user
 
 now we create objects for date like currentDate,expiringDate
 
 we use jwts builder to generate token , we pass args as subject as email from user,issued at as current date,expiration as expiring date
 
 and only decode by us , signin key/secret key using constructor call
 
 at last we return generated token
 
 
 */
@Component
public class JwtTokenProvider
{
	
	private final SecretKey secretKey;
	
	// JWT expiration time set to 3 days (259200000 milliseconds)
	private final long jwtExpirationMs = 259200000; // 3 days = 3 * 24 * 60 * 60 * 1000

	
	//constructor for generating secret key
    public JwtTokenProvider() 
    {
        String secretString = "843567893696976453275974432697R634976R738467TR678T34865R6834R8763T478378637664538745673865783678548735687R3";
        
        byte[] keyBytes = Base64.getDecoder().decode(secretString.getBytes(StandardCharsets.UTF_8));
    
        this.secretKey = new SecretKeySpec(keyBytes, "HmacSHA256");
    }
	
	
	
  
    public String generateToken(Authentication authentication) {
        String email = authentication.getName();

        // Extract roles/authorities
        String roles = authentication.getAuthorities().stream()
                          .map(GrantedAuthority::getAuthority)
                          .collect(Collectors.joining(","));

        Date currentDate = new Date();
        Date expiringDate = new Date(currentDate.getTime() + jwtExpirationMs);

        // Generate JWT with roles in claims
        String token = Jwts.builder()
        	    .setSubject(email)
        	    .claim("roles", roles)
        	    .setIssuedAt(currentDate)
        	    .setExpiration(expiringDate)
        	    .signWith(secretKey)
        	    .compact();


        return token;
    }

	public String extractEmailFromToken(String token)
	{
		 Claims claims = Jwts.parserBuilder()
	                .setSigningKey(secretKey)
	                .build()
	                .parseClaimsJws(token)
	                .getBody();

	        return claims.getSubject();

		
	}
	
	
	public boolean validateToken(String token) {
	    try {
	        // Parse and validate the token
	        Claims claims = Jwts.parserBuilder()
	            .setSigningKey(secretKey)
	            .build()
	            .parseClaimsJws(token)
	            .getBody();

	        // ✅ Extract roles to verify if they exist
	        String roles = claims.get("roles", String.class);
	        if (roles == null || roles.isEmpty()) {
	            System.out.println("Token does not contain roles.");
	            return false;
	        }

	        return true; // Token is valid and contains roles

	    } catch (io.jsonwebtoken.ExpiredJwtException e) {
	        System.out.println("Token expired: " + e.getMessage());
	    } catch (io.jsonwebtoken.UnsupportedJwtException e) {
	        System.out.println("Unsupported JWT: " + e.getMessage());
	    } catch (io.jsonwebtoken.MalformedJwtException e) {
	        System.out.println("Malformed JWT: " + e.getMessage());
	    } catch (io.jsonwebtoken.SignatureException e) {
	        System.out.println("Invalid signature: " + e.getMessage());
	    } catch (IllegalArgumentException e) {
	        System.out.println("Token is null or empty: " + e.getMessage());
	    }

	    return false; // Token is invalid
	}


}
