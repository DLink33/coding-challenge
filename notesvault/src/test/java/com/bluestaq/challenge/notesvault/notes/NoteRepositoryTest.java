package com.bluestaq.challenge.notesvault.notes;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace;
import org.springframework.test.context.ActiveProfiles;

import com.bluestaq.challenge.notesvault.notes.entity.NoteEntity;
import com.bluestaq.challenge.notesvault.notes.repo.NoteRepository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.UUID;

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
    NoteEntity note = new NoteEntity();
    note.setContent("hello");
    note.setId(UUID.randomUUID().toString());
    note.setCreatedAt(Instant.now());
    //.save() returns the saved entity
    NoteEntity saved = noteRepository.save(note);

    String savedId = saved.getId();
    String savedContent = saved.getContent();
    Instant savedCreatedAt = saved.getCreatedAt();

    assertThat(savedId).isNotBlank();
    assertThat(saved.getCreatedAt()).isNotNull();
    assertThat(saved.getContent()).isEqualTo("hello");

    var loaded = noteRepository.findById(savedId);
    assertThat(loaded).isPresent();
    assertThat(loaded.get().getContent()).isEqualTo(savedContent);
    assertThat(loaded.get().getCreatedAt()).isEqualTo(savedCreatedAt);
  }
}