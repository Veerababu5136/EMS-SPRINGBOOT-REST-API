package com.restapi.eventManagementSystem.controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.restapi.eventManagementSystem.entites.EventRegister;
import com.restapi.eventManagementSystem.entites.Events;
import com.restapi.eventManagementSystem.services.EventService;

@RestController
@RequestMapping("/api")
public class EventsController {

    @Autowired
    private EventService eventService;

    @Value("${upload.dir}")
    private String uploadDir;
    // Get All Events
    @GetMapping("/events")
    public ResponseEntity<?> getAllEvents() {
        List<Events> events = eventService.getEvents();
        if (events.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body(Map.of("status", HttpStatus.NO_CONTENT.value(), "message", "No Events Available"));
        }
        return ResponseEntity.ok(Map.of("status", HttpStatus.OK.value(), "message", "Events Retrieved Successfully", "events", events));
    }

    // Get Event by ID
    @GetMapping("/events/{id}")
    public ResponseEntity<?> getEventById(@PathVariable long id) {
        Optional<Events> event = eventService.getEventById(id);
        if (event.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("status", HttpStatus.NOT_FOUND.value(), "message", "Event Not Found"));
        }
        return ResponseEntity.ok(Map.of("status", HttpStatus.OK.value(), "message", "Event Found", "event", event.get()));
    }

    @PostMapping("/events")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createEvent(@RequestBody Events events, @RequestParam("file") MultipartFile imageFile) {
        try {
            // Validate event data
            String validationError = validateEvent(events);
            if (validationError != null) {
                return ResponseEntity.badRequest().body(Map.of("status", HttpStatus.BAD_REQUEST.value(), "message", validationError));
            }

            String uploadDir = "src/main/resources/static/images/"; // Adjust this path based on your project structure
            String fileName = imageFile.getOriginalFilename();
            Path filePath = Paths.get(uploadDir + fileName);
            
            // Create directories if they don’t exist
            Files.createDirectories(filePath.getParent());
            
            // Save the file to the file system
            Files.write(filePath, imageFile.getBytes());
            
            // Set the image URL to be stored in the database (relative to your resources/static/images)

            
            events.setImageName("/images/" + fileName);
            
            // Insert event into database
            boolean status = eventService.insertEvent(events);
            if (status) {
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(Map.of("status", HttpStatus.CREATED.value(), "message", "Event Added Successfully", "image_url", "/uploads/" + fileName));
            }

            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("status", HttpStatus.CONFLICT.value(), "message", "Error in Creating Event"));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", HttpStatus.INTERNAL_SERVER_ERROR.value(), "message", "File upload failed: " + e.getMessage()));
        }
    }


    // Update Event (Admin Only)
    @PutMapping("/events/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateEvent(@PathVariable long id, @RequestBody Events events) {
        try {
            String validationError = validateEvent(events);
            if (validationError != null) {
                return ResponseEntity.badRequest().body(Map.of("status", HttpStatus.BAD_REQUEST.value(), "message", validationError));
            }

            Optional<Events> existingEvent = eventService.getEventById(id);
            if (existingEvent.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("status", HttpStatus.NOT_FOUND.value(), "message", "Event not found"));
            }

            events.setId(id);
            boolean status = eventService.updateEvent(events);
            if (status) {
                return ResponseEntity.ok(Map.of("status", HttpStatus.OK.value(), "message", "Event Updated Successfully"));
            }
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("status", HttpStatus.CONFLICT.value(), "message", "Error in Updating Event"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", HttpStatus.INTERNAL_SERVER_ERROR.value(), "message", "An unexpected error occurred: " + e.getMessage()));
        }
    }

    // Delete Event (Admin Only)
    @DeleteMapping("/events/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteEvent(@PathVariable long id) {
        try {
            boolean isDeleted = eventService.deleteEvent(id);
            if (isDeleted) {
                return ResponseEntity.ok(Map.of("status", HttpStatus.OK.value(), "message", "Event Deleted Successfully"));
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("status", HttpStatus.NOT_FOUND.value(), "message", "Event Not Found"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", HttpStatus.INTERNAL_SERVER_ERROR.value(), "message", "An unexpected error occurred: " + e.getMessage()));
        }
    }

    // Event Registration
    @PostMapping("/eventRegister")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> eventRegister(@RequestBody EventRegister eventDto) {
        String validationMessage = validateEventRegister(eventDto);
        if (validationMessage != null) {
            return ResponseEntity.badRequest().body(Map.of("status", HttpStatus.BAD_REQUEST.value(), "message", validationMessage));
        }

        try {
            boolean alreadyRegistered = eventService.checkEventStudent(eventDto.getEventId(), eventDto.getStudentId());
            if (alreadyRegistered) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("status", HttpStatus.CONFLICT.value(), "message", "User already registered"));
            }

            boolean transactionExists = eventService.checkTid(eventDto.getTransactionId());
            if (!transactionExists) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("status", HttpStatus.CONFLICT.value(), "message", "Transaction already exists"));
            }

            boolean inserted = eventService.registerEvent(eventDto);
            if (!inserted) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("status", HttpStatus.INTERNAL_SERVER_ERROR.value(), "message", "Failed to register event"));
            }

            return ResponseEntity.ok(Map.of("status", HttpStatus.OK.value(), "message", "Event registered successfully!"));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("status", HttpStatus.CONFLICT.value(), "message", "Duplicate entry or constraint violation"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", HttpStatus.INTERNAL_SERVER_ERROR.value(), "message", "An error occurred: " + e.getMessage()));
        }
    }

    // Helper method to validate event data
    private String validateEvent(Events event) {
        if (event.getEventName() == null || event.getEventName().isEmpty()) return "Event name is required";
        if (event.getEndDate() == null) return "Event date is required";
        if (event.getDescription() == null || event.getDescription().isEmpty()) return "Event location is required";
        return null;
    }

    // Helper method to validate event registration
    private String validateEventRegister(EventRegister eventRegister) {
        if (eventRegister.getEventId() <= 0) return "Invalid event ID";
        if (eventRegister.getStudentId() <= 0) return "Invalid student ID";
        if (eventRegister.getTransactionId() == null || eventRegister.getTransactionId().isEmpty()) return "Transaction ID is required";
        return null;
    }
}
