package com.restapi.eventManagementSystem.controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
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

    @PostMapping(value = "/events", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createEvent(
            @RequestParam("eventName") String eventName,
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate,
            @RequestParam("description") String description,
            @RequestParam("applyLink") String applyLink,
            @RequestParam("file") MultipartFile file) {

        try {
            // Create the Events object with the form data
            Events events = new Events();
            events.setEventName(eventName);
            events.setStartDate(startDate);
            events.setEndDate(endDate);
            events.setDescription(description);
            events.setApplyLink(applyLink);

            // Validate event data
            String validationError = validateEvent(eventName, startDate, endDate, description, applyLink, file);
            if (validationError != null) {
                return ResponseEntity.badRequest().body(Map.of("status", HttpStatus.BAD_REQUEST.value(), "message", validationError));
            }

            // Define the upload directory (not inside `src/`)
            String uploadDir = "uploads/";
            File directory = new File(uploadDir);
            if (!directory.exists()) {
                directory.mkdirs(); // Ensure the `uploads` directory exists
            }

            // Generate a unique filename
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(uploadDir, fileName);

            // Save the file
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Store only the filename in the database
            events.setImageName(fileName);

            // Insert event into the database
            boolean status = eventService.insertEvent(events);
            if (status) {
                // Return public image URL
                String imageUrl = "/uploads/" + fileName;
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(Map.of("status", HttpStatus.CREATED.value(), "message", "Event Added Successfully", "image_url", imageUrl));
            }

            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("status", HttpStatus.CONFLICT.value(), "message", "Error in Creating Event"));

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", HttpStatus.INTERNAL_SERVER_ERROR.value(), "message", "File upload failed: " + e.getMessage()));
        }
    }

  

    @PutMapping(value = "/events", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateEvent(
            @RequestParam("eventId") Long eventId,
            @RequestParam("eventName") String eventName,
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate,
            @RequestParam("description") String description,
            @RequestParam("applyLink") String applyLink,
            @RequestParam(value = "file", required = false) MultipartFile file) {

        try {
        	
        	 // Validate event data
            String validationError = validateEvent(eventName, startDate, endDate, description, applyLink, file);
            if (validationError != null) {
                return ResponseEntity.badRequest().body(Map.of("status", HttpStatus.BAD_REQUEST.value(), "message", validationError));
            }
            // Fetch existing event from the database
            Events existingEvent = eventService.getEventById(eventId);
            if (existingEvent == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("status", HttpStatus.NOT_FOUND.value(), "message", "Event not found"));
            }

            // Update event details
            existingEvent.setEventName(eventName);
            existingEvent.setStartDate(startDate);
            existingEvent.setEndDate(endDate);
            existingEvent.setDescription(description);
            existingEvent.setApplyLink(applyLink);
           

            // Handle file update
            if (file != null && !file.isEmpty()) {
                // Define upload directory
                String uploadDir = "uploads/";
                File directory = new File(uploadDir);
                if (!directory.exists()) {
                    directory.mkdirs();
                }

                // Delete old image if it exists
                if (existingEvent.getImageName() != null) {
                    File oldFile = new File(uploadDir + existingEvent.getImageName());
                    if (oldFile.exists()) {
                        oldFile.delete();
                    }
                }

                // Generate new filename
                String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                Path filePath = Paths.get(uploadDir, fileName);

                // Save new image
                Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                // Update image name in database
                existingEvent.setImageName(fileName);
            }

            // Update event in database
            boolean status = eventService.updateEvent(existingEvent);
            if (status) {
                String imageUrl = "/uploads/" + existingEvent.getImageName();
                return ResponseEntity.ok(Map.of("status", HttpStatus.OK.value(), "message", "Event Updated Successfully", "image_url", imageUrl));
            }

            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("status", HttpStatus.CONFLICT.value(), "message", "Error in Updating Event"));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", HttpStatus.INTERNAL_SERVER_ERROR.value(), "message", "File update failed: " + e.getMessage()));
        }
    }


    @DeleteMapping("/events/{eventId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteEvent(@PathVariable Long eventId) {
        try {
            // Fetch the existing event
            Events existingEvent = eventService.getEventById(eventId);
            if (existingEvent == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("status", HttpStatus.NOT_FOUND.value(), "message", "Event not found"));
            }

            // Delete the image file from uploads folder if it exists
            if (existingEvent.getImageName() != null) {
                String uploadDir = "uploads/";
                File imageFile = new File(uploadDir + existingEvent.getImageName());
                if (imageFile.exists()) {
                    imageFile.delete();
                }
            }

            // Delete the event from the database
            boolean status = eventService.deleteEvent(eventId);
            if (status) {
                return ResponseEntity.ok(Map.of("status", HttpStatus.OK.value(), "message", "Event Deleted Successfully"));
            }

            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("status", HttpStatus.CONFLICT.value(), "message", "Error in Deleting Event"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", HttpStatus.INTERNAL_SERVER_ERROR.value(), "message", "Error: " + e.getMessage()));
        }
    }


    // Event Registration
    @PostMapping("/eventRegister")
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
    private String validateEvent(String eventName, String startDate, String endDate, String description, String applyLink, MultipartFile file) {
        if (eventName == null || eventName.trim().isEmpty()) {
            return "Event name is required";
        }
        if (startDate == null || startDate.trim().isEmpty()) {
            return "Start date is required";
        }
        if (endDate == null || endDate.trim().isEmpty()) {
            return "End date is required";
        }
        if (description == null || description.trim().isEmpty()) {
            return "Event description is required";
        }
        if (applyLink == null || applyLink.trim().isEmpty()) {
            return "Apply link is required";
        }
        if (file == null || file.isEmpty()) {
            return "Image file is required";
        }

        // Validate date format and logic
        try {
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);

            if (start.isAfter(end)) {
                return "Start date must be before end date";
            }
        } catch (DateTimeParseException e) {
            return "Invalid date format. Use YYYY-MM-DD";
        }

        return null; // No validation errors
    }

    // Helper method to validate event registration
    private String validateEventRegister(EventRegister eventRegister) {
        if (eventRegister.getEventId() <= 0) return "Invalid event ID";
        if (eventRegister.getStudentId() <= 0) return "Invalid student ID";
        if (eventRegister.getTransactionId() == null || eventRegister.getTransactionId().isEmpty()) return "Transaction ID is required";
        return null;
    }
}
