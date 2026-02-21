package com.bluestaq.challenge.notesvault.notes;

import java.time.Instant;

public record NoteResponse(
    String id,
    String content,
    Instant createdAt
) {}
