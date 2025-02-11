package com.restapi.eventManagementSystem.controllers;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthorizationController 
{
	
	@GetMapping("/api/admin")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> checking()
	{
		return ResponseEntity.ok(
	             Map.of("status", HttpStatus.OK.value(), "message", "Authorizedd")
	         );
		
	}

}
