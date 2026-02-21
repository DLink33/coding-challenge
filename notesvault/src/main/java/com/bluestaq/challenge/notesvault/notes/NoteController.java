package com.bluestaq.challenge.notesvault.notes;

import java.net.URI;
import java.time.Instant;
import java.util.UUID;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notes")
public class NoteController {

  @PostMapping
  public ResponseEntity<NoteResponse> create(@Valid @RequestBody CreateNoteRequest req) {
    // temporary “fake persistence” so we can get off 404 and prove the wiring

    // TODO: implement real persistence and remove this code
    String id = UUID.randomUUID().toString();
    NoteResponse body = new NoteResponse(id, req.content().trim(), Instant.now());

    System.out.println("REQ content=[" + req.content() + "]");

    return ResponseEntity
        .created(URI.create("/notes/" + id))
        .body(body);
  }
}