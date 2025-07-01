package feddericovonwernich.example.storyservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Represents a story entity containing metadata and its association with a story request.
 */
@Getter
@Setter
@Entity
@Table(name = "stories")
public class Story {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The title of the story. */
    private String title;

    /** The content of the story, stored as text. */
    @Column(columnDefinition = "TEXT")
    private String content;

    /** The source of the story. */
    @Enumerated(EnumType.STRING)
    private StorySource source;

    /** The associated story request for this story. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id")
    private StoryRequest request;

    /** The timestamp when the story was created. */
    private LocalDateTime createdAt;

    /** The timestamp when the story was last updated. */
    private LocalDateTime updatedAt;

    @PrePersist
    void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

}
