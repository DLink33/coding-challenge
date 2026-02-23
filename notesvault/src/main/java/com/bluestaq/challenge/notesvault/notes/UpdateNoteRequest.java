package com.bluestaq.challenge.notesvault.notes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.validation.constraints.NotBlank;

@JsonIgnoreProperties(ignoreUnknown = true)
public record UpdateNoteRequest(
    @NotBlank(message = "content must not be blank")  
    String content
    )
{}
