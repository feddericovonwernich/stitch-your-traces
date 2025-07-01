package feddericovonwernich.example.storyservice.dto;

import feddericovonwernich.example.storyservice.model.StorySource;

import java.time.LocalDateTime;

public record StoryResponse(
    Long id,
    String title,
    String content,
    StorySource source,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) { }
