package com.bluestaq.challenge.notesvault;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

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
}