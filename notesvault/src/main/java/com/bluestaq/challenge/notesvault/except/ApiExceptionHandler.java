package com.bluestaq.challenge.notesvault.except;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// This class is a global exception handler for the API. It uses @RestControllerAdvice to apply to all controllers.
@RestControllerAdvice
public class ApiExceptionHandler {

  // This method handles validation errors that occur when the request body 
  // does not meet the specified constraints (e.g., @NotBlank).
  // It extracts the error message from the exception and returns a 400 
  // Bad Request response with a JSON body containing the error message.
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException ex) {
    String msg = ex.getBindingResult()
        .getFieldErrors()
        .stream()
        .findFirst()
        .map(err -> err.getField() + ": " + err.getDefaultMessage())
        .orElse("Invalid request");

    return ResponseEntity.badRequest().body(Map.of("error", msg));
  }

  // This method handles NoteNotFoundException, which is thrown when a note with a specified ID is not found.
  // It returns a 404 Not Found response with a JSON body containing the error message.
  @ExceptionHandler(NoteNotFoundException.class)
  public ResponseEntity<Map<String, String>> handleNotFound(NoteNotFoundException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(Map.of("error", ex.getMessage()));
  }

  // This method handles InvalidNoteContentException, which is thrown when the content of a note is invalid (e.g., blank).
  // It returns a 400 Bad Request response with a JSON body containing the error message.
  @ExceptionHandler(InvalidNoteContentException.class)
  public ResponseEntity<Map<String, String>> handleInvalidContent(InvalidNoteContentException ex) {
      return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
  }

}