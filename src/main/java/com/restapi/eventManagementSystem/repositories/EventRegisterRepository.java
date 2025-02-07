package com.restapi.eventManagementSystem.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.restapi.eventManagementSystem.entites.EventRegister;

@Repository
public interface EventRegisterRepository extends JpaRepository<EventRegister, Long> {
    Optional<EventRegister> findByEventIdAndStudentId(long eventId, long studentId);

    Optional<EventRegister> findByTransactionId(String id);
}
