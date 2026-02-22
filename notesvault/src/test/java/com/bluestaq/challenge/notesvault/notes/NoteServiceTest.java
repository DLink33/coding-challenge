package com.bluestaq.challenge.notesvault.notes;

import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
public class NoteServiceTest {

    @Mock
    NoteRepository noteRepository;

    @InjectMocks
    NoteService noteService;

    @Test
    void crateNote_withValidContent_andSavesNoteToRepo() {
        when(noteRepository.save(any(NoteEntity.class)))
            .thenAnswer(inv -> inv.getArgument(0, NoteEntity.class));

        NoteEntity saved = noteService.createNote("  hello world  ");

        assertThat(saved.getId()).isNotBlank();
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getContent()).isEqualTo("hello world");

        ArgumentCaptor<NoteEntity> noteCaptor = ArgumentCaptor.forClass(NoteEntity.class);
        verify(noteRepository,times(1)).save(noteCaptor.capture());
        NoteEntity capturedNote = noteCaptor.getValue();
        assertThat(capturedNote.getId()).isNotBlank();
        assertThat(capturedNote.getCreatedAt()).isNotNull();
        assertThat(capturedNote.getContent()).isEqualTo("hello world");
    }

    @Test
    void createNote_withBlankContent_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> noteService.createNote("     "))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("content must not be blank");
        verify(noteRepository, never()).save(any());
    }

    @Test
    void createNote_withNullContent_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> noteService.createNote(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("content must not be blank");
        verify(noteRepository, never()).save(any());
    }

    @Test
    void getNoteById_withValidId_returnsNote() {
        NoteEntity note = new NoteEntity();
        note.setId("1");
        note.setCreatedAt(Instant.parse("1991-10-27T00:00:00Z"));
        note.setContent("hello");

        when(noteRepository.findById("1")).thenReturn(java.util.Optional.of(note));

        NoteEntity result = noteService.getNoteById("1");

        assertThat(result.getId()).isEqualTo("1");
        assertThat(result.getCreatedAt()).isEqualTo(Instant.parse("1991-10-27T00:00:00Z"));
        assertThat(result.getContent()).isEqualTo("hello");

        verify(noteRepository, times(1)).findById("1");
    }

    @Test
    void getNoteById_withInvalidId_throwsNoteNotFoundException() {
    String invalidId = "non-existent-id";
    when(noteRepository.findById(invalidId)).thenReturn(java.util.Optional.empty());

    assertThatThrownBy(() -> noteService.getNoteById(invalidId))
        .isInstanceOf(com.bluestaq.challenge.notesvault.except.NoteNotFoundException.class)
        .hasMessageContaining(invalidId);

    verify(noteRepository, times(1)).findById(invalidId);
    }

    @Test
    void listNotes_returnsNewestFirst() {
        NoteEntity older = new NoteEntity();
        older.setId("1");
        older.setCreatedAt(Instant.parse("1991-10-07T00:00:00Z"));
        older.setContent("older");

        NoteEntity newer = new NoteEntity();
        newer.setId("2");
        newer.setCreatedAt(Instant.parse("1991-10-27T00:00:10Z"));
        newer.setContent("newer");

        when(noteRepository.findAllByOrderByCreatedAtDesc())
            .thenReturn(List.of(newer, older));

        List<NoteEntity> notes = noteService.listNotes();

        assertThat(notes).extracting(NoteEntity::getId).containsExactly("2", "1");
        verify(noteRepository, times(1)).findAllByOrderByCreatedAtDesc();
    }   

}
