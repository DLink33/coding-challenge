package com.bluestaq.challenge.notesvault.notes;


import java.util.UUID;
import java.time.Instant;

import org.springframework.stereotype.Service;

@Service
public class NoteService {

    private final NoteRepository noteRepository;

    public NoteService(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    public NoteEntity createNote(String rawContent) {
        // normalize
        String content = (rawContent == null) ? "" : rawContent.trim();

        // @NotBlank should catch this in the pipeline, but we will add this here
        // for Justin Case
        if (content.isEmpty()) {
            throw new IllegalArgumentException("content must not be blank");
        }

        NoteEntity note = new NoteEntity(); // use the no-args constructor for JPA
        note.setContent(content);

        // we are going to let the service own these fields
        note.setId(UUID.randomUUID().toString());
        note.setCreatedAt(Instant.now());

        // save to db through NoteRepo and return
        return noteRepository.save(note);

    }
}
