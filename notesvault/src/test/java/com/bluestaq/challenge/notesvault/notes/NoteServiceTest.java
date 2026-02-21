package com.bluestaq.challenge.notesvault.notes;

import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

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

}
