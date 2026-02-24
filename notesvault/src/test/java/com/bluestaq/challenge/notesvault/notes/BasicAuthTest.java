package com.bluestaq.challenge.notesvault.notes;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;


@SpringBootTest
@AutoConfigureMockMvc
public class BasicAuthTest {

  @Autowired MockMvc mockMvc;

  @Test
  void listNotes_withoutAuth_returns401() throws Exception {
    mockMvc.perform(
        get("/v1/notes"))
          .andExpect(status().isUnauthorized());
  }

  @Test
  void listNotes_withAuth_resurns200() throws Exception {
    mockMvc.perform(
      get("/v1/notes").with(httpBasic("admin", "admin")))
        .andExpect(status().isOk()
    );
  }

}
