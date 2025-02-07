package com.restapi.eventManagementSystem.controllers;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@ControllerAdvice
public class Exceptions{

    // Handle invalid API endpoints (NoHandlerFoundException)
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<?> handleNoHandlerFoundException(NoHandlerFoundException ex) {
        return new ResponseEntity<>(
            Map.of(
                "status", HttpStatus.NOT_FOUND.value(),
                "message", "The API endpoint " + ex.getRequestURL() + " is invalid"
            ),
            HttpStatus.NOT_FOUND
        );
    }

    // Handle wrong HTTP method (HttpRequestMethodNotSupportedException)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<?> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        return new ResponseEntity<>(
            Map.of(
                "status", HttpStatus.METHOD_NOT_ALLOWED.value(),
                "message", "The HTTP method " + ex.getMethod() + " is not supported for this API"
            ),
            HttpStatus.METHOD_NOT_ALLOWED
        );
    }

    // Handle any other exception (generic error)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneralException(Exception ex) {
        return new ResponseEntity<>(
            Map.of(
                "status", HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "message", "An unexpected error occurred: " + ex.getMessage()
            ),
            HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
