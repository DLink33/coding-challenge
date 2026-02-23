package com.bluestaq.challenge.notesvault.except;

public class InvalidNoteContentException extends RuntimeException {
  public InvalidNoteContentException(String message) {
      super(message);
  }
}