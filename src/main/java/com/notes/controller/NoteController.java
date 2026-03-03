package com.notes.controller;

import com.notes.dto.NoteRequest;
import com.notes.model.Note;
import com.notes.repository.NoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notes")
@RequiredArgsConstructor
public class NoteController {
    private final NoteRepository noteRepo;

    @PostMapping
    public Note create(@RequestBody NoteRequest request) {

        String userId = (String) SecurityContextHolder
                .getContext().getAuthentication()
                .getPrincipal();

        Note note = Note.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .userId(userId)
                .isDeleted(false)
                .build();

        return noteRepo.save(note);
    }
    @GetMapping
    public List<Note> getMyNotes() {

        String userId = (String) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();

        return noteRepo.findByUserIdAndIsDeletedFalse(userId);
    }
    @PutMapping("/{id}")
    public Note update(@PathVariable String id,
                       @RequestBody NoteRequest request) {

        String userId = (String) SecurityContextHolder
                .getContext().getAuthentication()
                .getPrincipal();

        Note note = noteRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Note not found"));

        if (!note.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }

        note.setTitle(request.getTitle());
        note.setContent(request.getContent());

        return noteRepo.save(note);
    }
    @DeleteMapping("/{id}")
    public String delete(@PathVariable String id) {

        String userId = (String) SecurityContextHolder
                .getContext().getAuthentication()
                .getPrincipal();

        Note note = noteRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Note not found"));

        if (!note.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }

        note.setDeleted(true);

        noteRepo.save(note);

        return "Deleted successfully";
    }
}
