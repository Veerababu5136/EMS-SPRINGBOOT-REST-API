package com.restapi.eventManagementSystem.controllers;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.restapi.eventManagementSystem.dto.JwtAuthResponse;
import com.restapi.eventManagementSystem.entites.Events;
import com.restapi.eventManagementSystem.entites.User;
import com.restapi.eventManagementSystem.security.JwtTokenProvider;
import com.restapi.eventManagementSystem.services.UserService;

@RestController
@RequestMapping("/user/auth")
public class UserController 
{
	
	@Autowired
	private UserService userService;
	

	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private JwtTokenProvider jwtTokenProvider;
	
	
	@PostMapping("/register")
	public ResponseEntity<?> registerUser(@RequestBody User user)
	{
		System.out.println("jio");
		
		try
		{
			 // 1. Validate data
	        String validationError = validateUser(user);
	        if (validationError != null) {
	            return new ResponseEntity<>(
	                Map.of(
	                    "status", HttpStatus.BAD_REQUEST.value(),
	                    "message", validationError
	                ),
	                HttpStatus.BAD_REQUEST
	            );
	        }
			
			 Optional<User> exists=userService.getUserByEmail(user.getEmail());
			 
			 if(exists.isPresent())
			 {
				 return new ResponseEntity<>(
			                Map.of(
			                    "status", HttpStatus.ALREADY_REPORTED.value(),
			                    "message", "User Email Already registered"
			                ),
			                HttpStatus.ALREADY_REPORTED
			            );
			 }
			 
			 exists=userService.getUserByRollNumber(user.getRollNumber());
			 
			 if(exists.isPresent())
			 {
				 return new ResponseEntity<>(
			                Map.of(
			                    "status", HttpStatus.ALREADY_REPORTED.value(),
			                    "message", "User Roll Number Already registered"
			                ),
			                HttpStatus.ALREADY_REPORTED
			            );
			 }
		
			 
			 
		User added=userService.insertUser(user);
		
		if(added==null)
		{
			 return new ResponseEntity<>(
		                Map.of(
		                    "status", HttpStatus.BAD_REQUEST.value(),
		                    "message", "Check User Fields"
		                ),
		                HttpStatus.BAD_REQUEST
		            );
		}
		
		return new ResponseEntity<>(
                Map.of(
                    "status", HttpStatus.CREATED.value(),
                    "message", "User Registered Successfully"
                ),
                HttpStatus.CREATED
            );
		
	}
		catch(Exception e)
		{
			 return new ResponseEntity<>(
			            Map.of(
			                "status", HttpStatus.INTERNAL_SERVER_ERROR.value(),
			                "message", "An unexpected error occurred: " + e.getMessage()
			            ),
			            HttpStatus.INTERNAL_SERVER_ERROR
			        );
		}
		
	}
	
	

	@PostMapping("/login")
//	public ResponseEntity<String> loginUser(@RequestBody User user)
	public ResponseEntity<?> loginUser(@RequestBody User user)
	{
		/*
		
		Next we use authenticate function from authenticationManager interface, it will call UserDetailsService Interface from security by using 
		
		UsernamePasswordAuthenticationToken()..In this user email and password we pass
		
		Next SecurityContextHolder has getContext() in this setAuthentication().. we pass above returned
		
		Next if authentication true then return response as ok with msg
		
		else catch block exe the unauthorized with msg
		
		------------
		
		Now we call genearte token method here and return token as json response
		
		For that we are creating JwtAuthResponse dto
		
		and changing return type in header
		
		 */
		try
		{
			
		
		Authentication authentication=authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));
		
		
		SecurityContextHolder.getContext().setAuthentication(authentication);
		
		
		System.out.println(authentication);
		
		String token=jwtTokenProvider.generateToken(authentication);
		
		
//		return new ResponseEntity<>("User Loginned Succesfully",HttpStatus.OK);
		
		return ResponseEntity.ok(new JwtAuthResponse(token));
		
		}
		catch(Exception e)
		{
			 return new ResponseEntity<>(
			            Map.of(
			                "status", HttpStatus.UNAUTHORIZED.value(),
			                "message", "Invalid Credentials"
			            ),
			            HttpStatus.UNAUTHORIZED
			        );
	
		}
		
	}
	
	
	@PostMapping("/token")
	public int getId(String token)
	{
		System.out.println(token+"kl");
		return 0;
	}

	 @GetMapping("/checking/{name}")
	    public String check(@PathVariable String name) 
	{
		return "Hi "+name;
		
	}
	
	// Using Helper method for validation of events data
	private String validateUser(User user) {
	    if (user.getEmail() == null || user.getEmail().isEmpty()) {
	        return "Email is required";
	    }
	    if (user.getPassword() == null || user.getPassword().isEmpty()) {
	        return "Password is required";
	    }
	    if (user.getRollNumber() == null || user.getRollNumber().isEmpty()) {
	        return "Roll Number is required";
	    }
	    return null; // Validation passed
	}

	
}
