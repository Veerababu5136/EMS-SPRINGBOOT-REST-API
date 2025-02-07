package com.restapi.eventManagementSystem.services;

import java.util.Optional;

import com.restapi.eventManagementSystem.entites.User;

public interface UserService {
	
	User insertUser(User user);
	
	 Optional<User>  getUserByEmail(String email);
	 
	 Optional<User> getUserByRollNumber(String rollNumber);
	

}
