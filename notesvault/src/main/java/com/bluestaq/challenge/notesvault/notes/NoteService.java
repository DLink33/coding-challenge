package com.bluestaq.challenge.notesvault.notes;


import java.util.List;
import java.util.UUID;
import java.time.Instant;

import org.springframework.stereotype.Service;

import com.bluestaq.challenge.notesvault.except.NoteNotFoundException;

// This class is responsible for the business logic related to notes. 
// It interacts with the NoteRepository to perform CRUD operations on notes.
// and throws exceptions when certain conditions are not met (e.g., note not found, invalid content).
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

    public NoteEntity getNoteById(String id) {
        return noteRepository.findById(id)
            .orElseThrow(() -> new NoteNotFoundException(id));
    }

    public List<NoteEntity> listNotes() {
        return noteRepository.findAllByOrderByCreatedAtDesc();
    }

}
