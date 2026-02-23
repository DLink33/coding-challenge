package com.bluestaq.challenge.notesvault.notes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import com.jayway.jsonpath.JsonPath;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.Instant;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class NoteControllerTest {

  @Autowired MockMvc mockMvc;
  @Autowired NoteRepository noteRepository;

  @AfterEach
  void tearDown() {
    noteRepository.deleteAll();
  }

  @Test 
  void createNote_persistsToDatabase() throws Exception {
    String body = 
    """
      {"content":"This is a test note."}
    """;

  // rslt stores the response from the POST request to create a new note. 
  // We expect this to return a 201 Created status, and the response body should contain the note's id, content, and createdAt timestamp.
  MvcResult rslt = mockMvc.perform(post("/notes")
      .contentType(MediaType.APPLICATION_JSON)
      .content(body))
    .andExpect(status().isCreated())
    .andExpect(jsonPath("$.id").isNotEmpty())
    .andExpect(jsonPath("$.content").value("This is a test note."))
    .andExpect(jsonPath("$.createdAt").isNotEmpty())
    .andReturn();

    String json = rslt.getResponse().getContentAsString();
    String id = JsonPath.read(json, "$.id");

    // We should be able to find the note in the database using the 
    // id returned in the response.
    NoteEntity saved = noteRepository.findById(id).orElseThrow();
    assertThat(saved.getContent()).isEqualTo("This is a test note.");
    assertThat(saved.getCreatedAt()).isNotNull();

  } 

  @Test
  void createNote_returns201_andBody_andLocation() throws Exception {
    String json = """
      { "content": "hello world" }
    """;

    mockMvc.perform(post("/notes")
        .contentType(MediaType.APPLICATION_JSON)
        .content(json))
      // Verify 201 Created status  
      .andExpect(status().isCreated())
      // Verify Location header is present and matches the expected pattern
      .andExpect(header().string("Location", Matchers.matchesPattern(".*/notes/.*")))
      // Verify the body has an id
      .andExpect(jsonPath("$.id").isNotEmpty())
      // Verify the content is correct
      .andExpect(jsonPath("$.content").value("hello world"))
      // Verify createdAt timestamp is present
      .andExpect(jsonPath("$.createdAt").isNotEmpty());
  }

  @Test
  void createNote_blankContent_returns400() throws Exception {
    // this will get trimmed to blank and should trigger the validation error
    String json = """
      { "content": "   " }
    """; 
    mockMvc.perform(post("/notes")
        .contentType(MediaType.APPLICATION_JSON)
        .content(json))
      // Verify that the response status is 400 Bad Request due to validation failure
      .andExpect(status().isBadRequest())
      // Verify that the error message contains "content" to indicate which field failed validation
      .andExpect(jsonPath("$.error").value(Matchers.containsString("content")));
  }

  @Test
  void getNoteById_returns200_andNote() throws Exception {
    // First, create a note directly in the database to ensure it exists for retrieval
    NoteEntity note = new NoteEntity();
    note.setContent("Test note for retrieval");
    note.setId("test-note-id");
    note.setCreatedAt(Instant.now());

    noteRepository.save(note);

    // Perform a GET request to retrieve the note by its ID
    mockMvc.perform(get("/notes/{id}", "test-note-id"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value("test-note-id"))
      .andExpect(jsonPath("$.content").value("Test note for retrieval"))
      .andExpect(jsonPath("$.createdAt").isNotEmpty());
  }

  @Test
  void getNoteById_returns404_ifNotFound() throws Exception {
    mockMvc.perform(get("/notes/{id}", "does-not-exist"))
      .andExpect(status().isNotFound())
      .andExpect(jsonPath("$.error").value(Matchers.containsString("does-not-exist")));
  }

  @Test
  void listNotes_returns200_andNewestFirst() throws Exception {
    NoteEntity older = new NoteEntity();
    older.setId("1");
    older.setContent("older");
    older.setCreatedAt(Instant.parse("2026-02-21T00:00:00Z"));
    noteRepository.save(older);

    NoteEntity newer = new NoteEntity();
    newer.setId("2");
    newer.setContent("newer");
    newer.setCreatedAt(Instant.parse("2026-02-21T00:00:10Z"));
    noteRepository.save(newer);

    mockMvc.perform(get("/notes"))
      .andExpect(status().isOk())
      // array size 2
      .andExpect(jsonPath("$", Matchers.hasSize(2)))
      // newest first
      .andExpect(jsonPath("$[0].id").value("2"))
      .andExpect(jsonPath("$[0].content").value("newer"))
      .andExpect(jsonPath("$[1].id").value("1"))
      .andExpect(jsonPath("$[1].content").value("older"));
  }

  @Test
  void deleteNoteById_returns204_andDeletes() throws Exception {
    NoteEntity note = new NoteEntity();
    note.setId("delete-me");
    note.setContent("to be deleted");
    note.setCreatedAt(Instant.now());
    noteRepository.save(note);

    mockMvc.perform(delete("/notes/{id}", "delete-me"))
      .andExpect(status().isNoContent());

    assertThat(noteRepository.findById("delete-me")).isEmpty();
  }
}
