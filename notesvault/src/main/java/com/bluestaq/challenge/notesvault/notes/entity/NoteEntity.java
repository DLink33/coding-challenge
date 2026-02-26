package com.bluestaq.challenge.notesvault.notes.entity;

import jakarta.persistence.Entity;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;


// This class needs to be implemented as a JPA entity to represent a note in the database

@Entity
@Table(name = "notes")
public class NoteEntity {

  @Id
  @Column(name = "id", nullable = false, updatable = false, length = 36) // UUIDs are typically 36 characters long
  private String id;

  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @NotBlank
  @Column(name = "content", nullable = false, columnDefinition = "TEXT")
  private String content;

  public NoteEntity() {
    // Default constructor for JPA
    // this is used by JPA to create instances of the entity 
    // when retrieving data from the database. 
    // No-argument constructors are required by JPA specifications.
  }

  //getters
  public String getId() {
    return id; 
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public String getContent() {
    return content;
  }

  //setters
  public void setContent(String content) {
    this.content = content;
  }

  public void setCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
  }
  
  public void setId(String id) {
    this.id = id;
  }

}
