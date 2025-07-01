package feddericovonwernich.example.storyservice.dto;

import feddericovonwernich.example.storyservice.model.StoryStatus;

import java.util.List;

public record StoryRequestResponse(Long id, StoryStatus status, List<Long> storyIds) { }
