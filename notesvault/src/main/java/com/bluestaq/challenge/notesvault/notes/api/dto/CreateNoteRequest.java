package com.bluestaq.challenge.notesvault.notes.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.validation.constraints.NotBlank;

// This class represents the request body for creating a new note.
// It uses @JsonIgnoreProperties to ignore any unknown properties in the JSON request body.
@JsonIgnoreProperties(ignoreUnknown = true)
public record CreateNoteRequest(
    @NotBlank(message = "content must not be blank")
    String content
    )
{}
