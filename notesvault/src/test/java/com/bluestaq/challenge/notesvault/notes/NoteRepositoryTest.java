package com.bluestaq.challenge.notesvault.notes;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = Replace.NONE) // Use the real database configuration for testing
class NoteRepositoryTest {

  @Autowired // Inject the real NoteRepository bean configured for testing
  // this allows us to test the actual database interactions, ensuring that 
  // our JPA mappings and repository methods work as expected
  private NoteRepository noteRepository;

  @Test // Test that saving a note and then retrieving it by ID works correctly
  void save_and_findById_roundTrip() {
    NoteEntity note = new NoteEntity("  hello  ");
    //.save() returns the saved entity, which includes any auto-generated fields (like id and createdAt)
    NoteEntity saved = noteRepository.save(note);

    assertThat(saved.getId()).isNotBlank();
    assertThat(saved.getCreatedAt()).isNotNull();
    assertThat(saved.getContent()).isEqualTo("hello");

    var loaded = noteRepository.findById(saved.getId());
    assertThat(loaded).isPresent();
    assertThat(loaded.get().getContent()).isEqualTo("hello");
  }
}