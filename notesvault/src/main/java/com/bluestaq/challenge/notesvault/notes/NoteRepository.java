package com.bluestaq.challenge.notesvault.notes;

import org.springframework.data.jpa.repository.JpaRepository;

// This interface will make it easier to perform CRUD operations on NoteEntity objects in the database
public interface NoteRepository extends JpaRepository<NoteEntity, String> {
  /*
  By extending JpaRepository, this repository inherits many useful methods:
    - save(NoteEntity entity)
    - findById(String id)
    - findAll()
    - deleteById(String id)
    - existsById(String id)
    - count()
  */
}
