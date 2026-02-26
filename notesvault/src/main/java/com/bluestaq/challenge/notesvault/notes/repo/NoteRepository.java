package com.bluestaq.challenge.notesvault.notes.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bluestaq.challenge.notesvault.notes.entity.NoteEntity;

// This interface will make it easier to perform CRUD operations on NoteEntity objects in the database
// Through this interface, we can save, find, delete, and perform other operations on NoteEntity instances.
// This is how we communicate with the database layer in a Spring application using JPA.
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
  List<NoteEntity> findAllByOrderByCreatedAtDesc();
}
