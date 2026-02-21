package com.bluestaq.challenge.notesvault.notes;

import jakarta.validation.constraints.NotBlank;

public record CreateNoteRequest(
    @NotBlank(message = "content must not be blank")
    String content
    )
{}
