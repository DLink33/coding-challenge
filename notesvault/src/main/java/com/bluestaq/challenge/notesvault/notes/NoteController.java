package com.bluestaq.challenge.notesvault.notes;

import java.net.URI;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


// This class is the REST controller for handling note-related HTTP requests.
@RestController
@RequestMapping("/notes")
public class NoteController {
  
  // this should be final since we are using constructor injection and it
  // should not change after construction
  private final NoteService noteService;

  public NoteController(NoteService noteService) {
    this.noteService = noteService;
  }

  // HTTP POST endpoint to create a new note. 
  // It accepts a CreateNoteRequest in the request body and returns a NoteResponse.
  @PostMapping
  public ResponseEntity<NoteResponse> create(@Valid @RequestBody CreateNoteRequest req) {

    NoteEntity savedNote = noteService.createNote(req.content());

    NoteResponse body = new NoteResponse(
      savedNote.getId(),
      savedNote.getCreatedAt(),
      savedNote.getContent()
    );

    return ResponseEntity
        .created(URI.create("/notes/" + savedNote.getId()))
        .body(body);
  }
}