package com.restapi.eventManagementSystem.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.restapi.eventManagementSystem.entites.User;
import com.restapi.eventManagementSystem.repositories.UserRepository;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // Inject PasswordEncoder bean

    @Override
    public User insertUser(User user) {
        // Encode the user's password before saving
    	user.setROLE("ROLE_USER");;
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }
	@Override
	public Optional<User> getUserByEmail(String email) {

		return userRepository.findByEmail(email);
	}
	@Override
	public Optional<User> getUserByRollNumber(String rollNumber) {
		// TODO Auto-generated method stub
		return userRepository.findByRollNumber(rollNumber);
	}
	

}
