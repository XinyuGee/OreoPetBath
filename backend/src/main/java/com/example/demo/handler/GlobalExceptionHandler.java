package com.example.demo.handler;

import com.example.demo.exception.BookingConflictException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(BookingConflictException.class)
  public ResponseEntity<Map<String, Object>> handleConflict(BookingConflictException ex) {
    return build(HttpStatus.CONFLICT, ex.getMessage());
  }

  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<Map<String, Object>> handleNotFound(EntityNotFoundException ex) {
    return build(HttpStatus.NOT_FOUND, "Requested resource not found.");
  }

  @ExceptionHandler(Exception.class) // fall-back
  public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
    return build(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error occurred.");
  }

  private ResponseEntity<Map<String, Object>> build(HttpStatus status, String msg) {
    return ResponseEntity.status(status)
        .body(Map.of(
            "timestamp", Instant.now().toString(),
            "status", status.value(),
            "error", status.getReasonPhrase(),
            "message", msg));
  }
}