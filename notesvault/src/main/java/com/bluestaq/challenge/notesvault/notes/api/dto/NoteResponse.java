package com.bluestaq.challenge.notesvault.notes.api.dto;

import java.time.Instant;

// DTO class to represent the response for a note. 
// This is used to send note data back to the client in a structured format.
public record NoteResponse(
    String id,
    Instant createdAt,
    String content
) {}
