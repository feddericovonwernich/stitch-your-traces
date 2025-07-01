package feddericovonwernich.example.storyservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a request for a story, containing metadata and associated stories.
 */
@Getter
@Setter
@Entity
@Table(name = "story_requests")
public class StoryRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The title of the story request. */
    private String title;

    /** Optional prompt or human text for the story request. */
    @Column(columnDefinition = "TEXT")
    private String content;

    /** The source of the story request. */
    @Enumerated(EnumType.STRING)
    private StorySource source;

    /** The current status of the story request. */
    @Enumerated(EnumType.STRING)
    private StoryStatus status;

    /** The set of stories associated with this request. */
    @OneToMany(mappedBy = "request")
    private Set<Story> stories = new HashSet<>();

    /** The timestamp when the story request was created. */
    private LocalDateTime createdAt;

    /** The timestamp when the story request was last updated. */
    private LocalDateTime updatedAt;

    public void addStory(Story story) {
        stories.add(story);
        story.setRequest(this);
    }

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
