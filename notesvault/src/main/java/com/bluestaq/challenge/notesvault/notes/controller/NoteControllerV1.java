package com.bluestaq.challenge.notesvault.notes.controller;

import java.net.URI;
import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.bluestaq.challenge.notesvault.except.NoteNotFoundException;
import com.bluestaq.challenge.notesvault.notes.api.dto.CreateNoteRequest;
import com.bluestaq.challenge.notesvault.notes.api.dto.NoteResponse;
import com.bluestaq.challenge.notesvault.notes.api.dto.UpdateNoteRequest;
import com.bluestaq.challenge.notesvault.notes.entity.NoteEntity;
import com.bluestaq.challenge.notesvault.notes.service.NoteService;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;



// This class is the REST controller for handling note-related HTTP requests.
@RestController
@RequestMapping("/v1/notes")
public class NoteControllerV1 {
  
  // this should be final since we are using constructor injection and it
  // should not change after construction
  private final NoteService noteService;

  public NoteControllerV1(NoteService noteService) {
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
        .created(URI.create("/v1/notes/" + savedNote.getId()))
        .body(body);
  }

  // HTTP GET endpoint to retrieve a note by its ID.
  // It returns a NoteResponse if the note is found, 
  // or a 404 Not Found response if the note does not exist.
  @GetMapping("/{id}")
  public NoteResponse getById(@PathVariable String id) {
    NoteEntity note = noteService.getNoteById(id);
    return new NoteResponse(
      note.getId(),
      note.getCreatedAt(),
      note.getContent()
    );
  }

  // HTTP GET endpoint to list all notes.
  // It returns a list of NoteResponse objects, ordered by creation time (newest first).
  @GetMapping
  public List<NoteResponse> listNotes() {
    return noteService.listNotes().stream()
      .map(note -> new NoteResponse(
        note.getId(),
        note.getCreatedAt(),
        note.getContent()
      ))
      .toList();
  }

  @PutMapping("/{id}")
  public ResponseEntity<NoteResponse> updateNoteById(@PathVariable String id, @Valid @RequestBody UpdateNoteRequest req) {
      NoteEntity updated = noteService.updateNoteById(id, req.content());
      return ResponseEntity.ok(new NoteResponse(
        updated.getId(),
        updated.getCreatedAt(),
        updated.getContent()
      ));
  }
  
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteById(@PathVariable String id) {
    try {
      noteService.deleteNoteById(id); // service decides if it exists
      return ResponseEntity.noContent().build();
    } catch (NoteNotFoundException e) {
      return ResponseEntity.notFound().build();
    }
  }

}


