package com.notes.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "notes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Note {
    @Id
    private String id;
    private String title;
    private String content;
    private String userId;
    private boolean isDeleted = false;
    private Instant createdAt = Instant.now();
    private Instant updatedAt = Instant.now();

}
