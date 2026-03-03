package com.notes.repository;

import com.notes.model.Note;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface NoteRepository extends MongoRepository<Note, String> {
    List<Note> findByUserIdAndIsDeletedFalse(String userId);
}
