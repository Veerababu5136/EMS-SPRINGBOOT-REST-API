package com.restapi.eventManagementSystem.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.restapi.eventManagementSystem.entites.User;

public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByEmail(String email);

	Optional<User> findByRollNumber(String rollNumber);

}
