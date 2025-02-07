package com.restapi.eventManagementSystem.controllers;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.restapi.eventManagementSystem.entites.EventRegister;
import com.restapi.eventManagementSystem.entites.Events;
import com.restapi.eventManagementSystem.services.EventService;

@RestController
@RequestMapping("/api")
public class EventsController {
	
	@Autowired
	private EventService eventService;
	
	
	
	@GetMapping("/events")
	public ResponseEntity<?> getAllEvents() {
	    List<Events> events = eventService.getEvents();

	    if (events.isEmpty()) {
	        return new ResponseEntity<>(
	            Map.of(
	                "status", HttpStatus.NO_CONTENT.value(),
	                "message", "No Events Available",
	                "events", events
	            ),
	            HttpStatus.NO_CONTENT
	        );
	    }

	    return new ResponseEntity<>(
	        Map.of(
	            "status", HttpStatus.OK.value(),
	            "message", "Events Retrieved Successfully",
	            "events", events
	        ),
	        HttpStatus.OK
	    );
	}

	
	
	
	@GetMapping("/events/{id}")
	public ResponseEntity<?> getEventById(@PathVariable long id)
	{
		Optional<Events> event=eventService.getEventById(id);
		
		System.out.println(event);
		
		if(event.isEmpty())
		{
			 return new ResponseEntity(
			        Map.of(
			                "status", HttpStatus.NOT_FOUND.value(),
			                "message", "Event Not Found"
			            ),
			            HttpStatus.NOT_FOUND
			        );		
			}
		
		 return new ResponseEntity<>(
			        Map.of(
			            "status", HttpStatus.OK.value(),
			            "message", "Event Found",
			            "event", event.get()
			        ),
			        HttpStatus.OK
			    );
		
	}
	
	
	@PostMapping("/events")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> createEvent(@RequestBody Events events) {
	    try {
	        // 1. Validate data using helper method
	        String validationError = validateEvent(events);
	        if (validationError != null) {
	            return new ResponseEntity<>(
	                Map.of(
	                    "status", HttpStatus.BAD_REQUEST.value(),
	                    "message", validationError
	                ),
	                HttpStatus.BAD_REQUEST
	            );
	        }

	        // 2. Adding the event
	        boolean status = eventService.insertEvent(events);

	        if (status) {
	            return new ResponseEntity<>(
	                Map.of(
	                    "status", HttpStatus.CREATED.value(),
	                    "message", "Event Added Successfully"
	                ),
	                HttpStatus.CREATED
	            );
	        }

	        // 3. Handling insertion failure
	        return new ResponseEntity<>(
	            Map.of(
	                "status", HttpStatus.CONFLICT.value(),
	                "message", "Error in Creating Event. Check Data"
	            ),
	            HttpStatus.CONFLICT
	        );

	    } catch (Exception e) {
	        // Handle unexpected errors
	        return new ResponseEntity<>(
	            Map.of(
	                "status", HttpStatus.INTERNAL_SERVER_ERROR.value(),
	                "message", "An unexpected error occurred: " + e.getMessage()
	            ),
	            HttpStatus.INTERNAL_SERVER_ERROR
	        );
	    }
	}
	
	@PutMapping("/events/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> updateEvent(@PathVariable long id, @RequestBody Events events) {
	    try {
	        // 1. Validate data
	        String validationError = validateEvent(events);
	        if (validationError != null) {
	            return new ResponseEntity<>(
	                Map.of(
	                    "status", HttpStatus.BAD_REQUEST.value(),
	                    "message", validationError
	                ),
	                HttpStatus.BAD_REQUEST
	            );
	        }

	        // 2. Check if event exists
	        Optional<Events> existingEvent = eventService.getEventById(id);
	        if (existingEvent.isEmpty()) {
	            return new ResponseEntity<>(
	                Map.of(
	                    "status", HttpStatus.NOT_FOUND.value(),
	                    "message", "Event not found"
	                ),
	                HttpStatus.NOT_FOUND
	            );
	        }

	        // 3. Update the event
	        events.setId(id); // Set the ID to ensure it updates the correct event
	        boolean status = eventService.updateEvent(events);

	        if (status) {
	            return new ResponseEntity<>(
	                Map.of(
	                    "status", HttpStatus.OK.value(),
	                    "message", "Event Updated Successfully"
	                ),
	                HttpStatus.OK
	            );
	        }

	        // 4. Handle update failure
	        return new ResponseEntity<>(
	            Map.of(
	                "status", HttpStatus.CONFLICT.value(),
	                "message", "Error in Updating Event. Check Data"
	            ),
	            HttpStatus.CONFLICT
	        );

	    } catch (Exception e) {
	        // Handle unexpected errors
	        return new ResponseEntity<>(
	            Map.of(
	                "status", HttpStatus.INTERNAL_SERVER_ERROR.value(),
	                "message", "An unexpected error occurred: " + e.getMessage()
	            ),
	            HttpStatus.INTERNAL_SERVER_ERROR
	        );
	    }
	}


	 @DeleteMapping("/{id}")
	 @PreAuthorize("hasRole('ADMIN')")
	    public ResponseEntity<?> deleteEvent(@PathVariable long id) {
	        try {
	            // Assuming eventService.deleteEvent returns a boolean indicating success
	            boolean isDeleted = eventService.deleteEvent(id);

	            if (isDeleted) {
	                return new ResponseEntity<>(
	                    Map.of(
	                        "status", HttpStatus.OK.value(),
	                        "message", "Event Deleted Successfully"
	                    ),
	                    HttpStatus.OK
	                );
	            } else {
	                return new ResponseEntity<>(
	                    Map.of(
	                        "status", HttpStatus.NOT_FOUND.value(),
	                        "message", "Event Not Found"
	                    ),
	                    HttpStatus.NOT_FOUND
	                );
	            }
	        } catch (Exception e) {
	            // Handle any unexpected errors
	            return new ResponseEntity<>(
	                Map.of(
	                    "status", HttpStatus.INTERNAL_SERVER_ERROR.value(),
	                    "message", "An unexpected error occurred: " + e.getMessage()
	                ),
	                HttpStatus.INTERNAL_SERVER_ERROR
	            );
	        }
	    }
	 
	 
	 
	 
	 //event Register 
	 
	 @PostMapping("/eventRegister")
	 @PreAuthorize("hasRole('ADMIN')")
	 public ResponseEntity<?> eventRegister(@RequestBody EventRegister eventDto) {
	     // Validate the event registration data
	     String validationMessage = validateEventRegister(eventDto);
	     if (validationMessage != null) {
	         return ResponseEntity.badRequest().body(
	             Map.of("status", HttpStatus.BAD_REQUEST.value(), "message", validationMessage)
	         );
	     }

	     try {
	         // Check if student is already registered for the event
	         boolean status = eventService.checkEventStudent(eventDto.getEventId(), eventDto.getStudentId());

	         if (status) {
	             return ResponseEntity.status(HttpStatus.CONFLICT).body(
	                 Map.of("status", HttpStatus.CONFLICT.value(), "message", "User already registered for this event")
	             );
	         }
	         
	         status=eventService.checkTid(eventDto.getTransactionId());
	 		System.out.println(status);

	         if(!status)
	         {
	        	 return ResponseEntity.status(HttpStatus.CONFLICT).body(
		                 Map.of("status", HttpStatus.CONFLICT.value(), "message", "Transaction already exists")
		             );
	         }
	         
	         
//	         System.out.println(eventDto);
	         
	         // Attempt to register the event
	         boolean inserted = eventService.registerEvent(eventDto);
	         
	         System.out.println(inserted);

	         if (!inserted) {
	             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
	                 Map.of("status", HttpStatus.INTERNAL_SERVER_ERROR.value(), "message", "Failed to register event")
	             );
	         }

	         // Successfully registered
	         return ResponseEntity.ok(
	             Map.of("status", HttpStatus.OK.value(), "message", "Event registered successfully!")
	         );

	     } catch (DataIntegrityViolationException e) {
	         return ResponseEntity.status(HttpStatus.CONFLICT).body(
	             Map.of("status", HttpStatus.CONFLICT.value(), "message", "Duplicate entry or constraint violation")
	         );
	     } catch (Exception e) {
	         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
	             Map.of("status", HttpStatus.INTERNAL_SERVER_ERROR.value(), "message", "An error occurred: " + e.getMessage())
	         );
	     }
	 }

	 // Helper method to validate event registration data
	 private String validateEventRegister(EventRegister eventRegister) 
	 {
	     if (eventRegister.getEventId() <= 0) {
	         return "Valid Event ID is required";
	     }
	     if (eventRegister.getStudentId() <= 0) {
	         return "Valid Student ID is required";
	     }
	     
	     if (eventRegister.getTransactionId()==null) {
	         return "Valid Transaction Id is required";
	     }
	     return null; // Validation passed
	 }

	

	

	// Using Helper method for validation of events data
	private String validateEvent(Events events) {
	    if (events.getEventName() == null || events.getEventName().isEmpty()) {
	        return "Event Name is required";
	    }
	    if (events.getImageName() == null || events.getImageName().isEmpty()) {
	        return "Image Name is required";
	    }
	    if (events.getStartDate() == null || events.getStartDate().isEmpty()) {
	        return "Start Date is required";
	    }
	    if (events.getEndDate() == null || events.getEndDate().isEmpty()) {
	        return "End Date is required";
	    }
	    if (events.getDescription() == null || events.getDescription().isEmpty()) {
	        return "Description is required";
	    }
	    return null; // Validation passed
	}

}
