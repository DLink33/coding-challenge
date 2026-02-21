package com.bluestaq.challenge.notesvault.notes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class NoteControllerTest {

  @Autowired MockMvc mockMvc;

  @Test
  void createNote_returns201_andBody_andLocation() throws Exception {
    String json = """
      { "content": "hello world" }
    """;

    mockMvc.perform(post("/notes")
        .contentType(MediaType.APPLICATION_JSON)
        .content(json))
      .andExpect(status().isCreated())
      .andExpect(header().string("Location", Matchers.matchesPattern(".*/notes/.*")))
      .andExpect(jsonPath("$.id").isNotEmpty())
      .andExpect(jsonPath("$.content").value("hello world"))
      .andExpect(jsonPath("$.createdAt").isNotEmpty());
  }

  @Test
  void createNote_blankContent_returns400() throws Exception {
    String json = """
      { "content": "   " }
    """;

    mockMvc.perform(post("/notes")
        .contentType(MediaType.APPLICATION_JSON)
        .content(json))
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.error").value(Matchers.containsString("content")));
  }
}