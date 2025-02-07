package com.restapi.eventManagementSystem.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.restapi.eventManagementSystem.entites.Events;

@Repository
public interface EventRepository extends JpaRepository<Events,Long> {

}
