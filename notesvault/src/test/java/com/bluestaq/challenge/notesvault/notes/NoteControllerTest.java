package com.bluestaq.challenge.notesvault.notes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class NoteControllerTest {

  @Autowired MockMvc mockMvc;
  @Autowired NoteRepository noteRepository;

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
}