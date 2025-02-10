package com.restapi.eventManagementSystem.config;
import org.springframework.beans.factory.annotation.Autowired;
/*
 
  In this two stages are there
  
  1.register
  2.login
  
  
  1.Register
  
  -- when user register then email,password will get to controller and in service we user passwordencoder(spring security class) to encode password
  
  beacause while login , spring security encodes user submiited password
  
  After register we will return user dto object with id,email,password(encoded)............
  
   
   2. Login
   
   In This three stages 
   
   i. validate user credentials
   
   ii.Generate Token
   
   iii.Validate Token
 
----------------------------------------------------------------------------------------------------------------------------------------------------
 i. Validate User Credentials  
----------------------------------------------------------------------------------------------------------------------------------------------------
        First AuthController receive user email,password using dto class
   
  	    Next we use authenticate function from authenticationManager interface, it will call UserDetailsService Interface from security by using 
		
		UsernamePasswordAuthenticationToken()..In this user email and password we pass
		
		Next SecurityContextHolder has getContext() in this setAuthentication().. we pass above returned
		
		Next if authentication true then return response as ok with msg
		
		else catch block exe the unauthorized with msg
 -----------------------------------------------------------------------------------------------------------------------------------------------------
  
  
  
  
 -----------------------------------------------------------------------------------------------------------------------------------------------------
 ii. Generate Token
 ------------------------------------------------------------------------------------------------------------------------------------------------------      
         Here we are generating jwt tokens for once per request acheieveing
         
         This once per request means stateless validation means no session is maintained
         
         For acheiving this once per request we use jwt tokens
         
         Jwt Stands for json web tokens
         
         Jwt will be implemented using io.jsonwebtoken dependency
         
         
         
 1. Create JwtTokenProvider class in security pkg it takes arg as authentication interface where it holds username() in this we get email from user
 
 now we create objects for date like currentDate,expiringDate
 
 we use jwts builder to generate token , we pass args as subject as email from user,issued at as current date,expiration as expiring date
 
 and only decode by us , signin key/secret key using constructor call
 
 at last we return generated token
 ------------------------------------------------------------------------------------------------------------------------------------------------------
 
 
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
 */
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.restapi.eventManagementSystem.security.JwtAuthFilter;

import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)  // Enable @PreAuthorize
public class SecurityConfig 
{
	
	@Autowired
	private JwtAuthFilter authFilter;
	
	    @Bean
	    public PasswordEncoder passwordEncoder() {
	        return new BCryptPasswordEncoder();
	    }
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
	    http
	        .csrf(AbstractHttpConfigurer::disable) // Disable CSRF for APIs
	        .cors(Customizer.withDefaults()) // Allow Cross-Origin requests
	        .authorizeHttpRequests(authz -> authz
	        		.requestMatchers("/uploads/**", "/css/**", "/js/**", "/images/**").permitAll() // Allow public access
	        	    .requestMatchers("/user/auth/**").permitAll()         // Public endpoints for login/register
	        	    .requestMatchers("/admin/**").hasRole("ADMIN")        // Only ADMIN can access /admin/**
	        	    .requestMatchers("/user/**").hasAnyRole("USER", "ADMIN") // USER and ADMIN can access /user/**
	        	    .anyRequest().authenticated()                        // All other endpoints require authentication
	        	

	            
	            
	        );
	    
	    http.addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class);

	    return http.build(); // Return the SecurityFilterChain
	}
	
	
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception
	{
		 return authenticationConfiguration.getAuthenticationManager();
		 
	}
	   

}
