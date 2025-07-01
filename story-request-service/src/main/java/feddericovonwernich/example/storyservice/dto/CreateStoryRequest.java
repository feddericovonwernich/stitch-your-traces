package feddericovonwernich.example.storyservice.dto;

import feddericovonwernich.example.storyservice.model.StorySource;

public record CreateStoryRequest(String title, String content, StorySource source) { }
