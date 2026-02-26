package com.bluestaq.challenge.notesvault.notes;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bluestaq.challenge.notesvault.except.NoteNotFoundException;
import com.bluestaq.challenge.notesvault.notes.entity.NoteEntity;
import com.bluestaq.challenge.notesvault.notes.repo.NoteRepository;
import com.bluestaq.challenge.notesvault.notes.service.NoteService;
import com.bluestaq.challenge.notesvault.except.InvalidNoteContentException;

@ExtendWith(MockitoExtension.class)
public class NoteServiceTest {

    @Mock
    private NoteRepository noteRepository;

    @InjectMocks
    private NoteService noteService;

    @Test
    void createNote_withValidContent_savesNoteToRepo() {
        when(noteRepository.save(any(NoteEntity.class)))
            .thenAnswer(inv -> inv.getArgument(0, NoteEntity.class));

        NoteEntity saved = noteService.createNote("  hello world  ");

        assertThat(saved.getId()).isNotBlank();
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getContent()).isEqualTo("hello world");

        verify(noteRepository).save(any(NoteEntity.class));
        verifyNoMoreInteractions(noteRepository);
    }

    @Test
    void createNote_withBlankContent_throwsInvalidNoteContentException() {
        assertThatThrownBy(() -> noteService.createNote("     "))
            .isInstanceOf(InvalidNoteContentException.class)
            .hasMessage("content must not be blank");

        verify(noteRepository, never()).save(any());
        verifyNoMoreInteractions(noteRepository);
    }

    @Test
    void createNote_withNullContent_throwsInvalidNoteContentException() {
        assertThatThrownBy(() -> noteService.createNote(null))
            .isInstanceOf(InvalidNoteContentException.class)
            .hasMessage("content must not be blank");

        verify(noteRepository, never()).save(any());
        verifyNoMoreInteractions(noteRepository);
    }

    @Test
    void getNoteById_withValidId_returnsNote() {
        NoteEntity note = new NoteEntity();
        note.setId("1");
        note.setCreatedAt(Instant.parse("1991-10-27T00:00:00Z"));
        note.setContent("hello");

        when(noteRepository.findById("1")).thenReturn(Optional.of(note));

        NoteEntity result = noteService.getNoteById("1");

        assertThat(result.getId()).isEqualTo("1");
        assertThat(result.getCreatedAt()).isEqualTo(Instant.parse("1991-10-27T00:00:00Z"));
        assertThat(result.getContent()).isEqualTo("hello");

        verify(noteRepository).findById("1");
        verifyNoMoreInteractions(noteRepository);
    }

    @Test
    void getNoteById_withInvalidId_throwsNoteNotFoundException() {
        String invalidId = "non-existent-id";
        when(noteRepository.findById(invalidId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> noteService.getNoteById(invalidId))
            .isInstanceOf(NoteNotFoundException.class)
            .hasMessageContaining(invalidId);

        verify(noteRepository).findById(invalidId);
        verifyNoMoreInteractions(noteRepository);
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

        verify(noteRepository).findAllByOrderByCreatedAtDesc();
        verifyNoMoreInteractions(noteRepository);
    }

    @Test
    void deleteNoteById_withValidId_deletesNote() {
        String idToDelete = UUID.randomUUID().toString();
        when(noteRepository.existsById(idToDelete)).thenReturn(true);

        noteService.deleteNoteById(idToDelete);

        verify(noteRepository).existsById(idToDelete);
        verify(noteRepository).deleteById(idToDelete);
        verifyNoMoreInteractions(noteRepository);
    }

    @Test
    void deleteNoteById_withInvalidId_throwsNoteNotFoundException() {
        String invalidId = "non-existent-id";
        when(noteRepository.existsById(invalidId)).thenReturn(false);

        assertThatThrownBy(() -> noteService.deleteNoteById(invalidId))
            .isInstanceOf(NoteNotFoundException.class)
            .hasMessageContaining(invalidId);

        verify(noteRepository).existsById(invalidId);
        verify(noteRepository, never()).deleteById(anyString());
        verifyNoMoreInteractions(noteRepository);
    }

    @Test
    void updateNoteById_withValidIdAndContent_updatesAndReturnsNote() {
        String id = UUID.randomUUID().toString();
        Instant createdAt = Instant.now();
        String rawContent = "  updated content  ";

        NoteEntity existingNote = new NoteEntity();
        existingNote.setId(id);
        existingNote.setCreatedAt(createdAt);
        existingNote.setContent("original content");

        when(noteRepository.findById(id)).thenReturn(Optional.of(existingNote));
        when(noteRepository.save(any(NoteEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        NoteEntity result = noteService.updateNoteById(id, rawContent);

        // Verify that the returned note has the updated content and the same ID and createdAt
        assertThat(result.getId()).isEqualTo(id);
        // createdAt should not change on update
        assertThat(result.getCreatedAt()).isEqualTo(createdAt);
        // content should be updated and trimmed
        assertThat(result.getContent()).isEqualTo("updated content");

        // after we have asserted that the result is correct, we can also verify that the 
        // repository methods were called as expected
        verify(noteRepository).findById(id);
        verify(noteRepository).save(existingNote);
        verifyNoMoreInteractions(noteRepository);
    }

    @Test
    void updateNoteById_withBlankContent_throwsInvalidNoteContentException() {
        String id = UUID.randomUUID().toString();

        assertThatThrownBy(() -> noteService.updateNoteById(id, "     "))
            .isInstanceOf(com.bluestaq.challenge.notesvault.except.InvalidNoteContentException.class)
            .hasMessage("content must not be blank");

        // explicitly verify the repository was never called since the validation should fail 
        // before any repository interaction
        verify(noteRepository, never()).findById(anyString());
        verify(noteRepository, never()).save(any());
        // verify that there are no interactions with the repository at all
        verifyNoMoreInteractions(noteRepository);
    }

    @Test
    void updateNoteById_withInvalidId_throwsNoteNotFoundException() {
        String id = "non-existent-id";
        when(noteRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> noteService.updateNoteById(id, "valid content"))
            .isInstanceOf(NoteNotFoundException.class)
            .hasMessageContaining(id);

        verify(noteRepository).findById(id);
        verify(noteRepository, never()).save(any());
        verifyNoMoreInteractions(noteRepository);
    }

}