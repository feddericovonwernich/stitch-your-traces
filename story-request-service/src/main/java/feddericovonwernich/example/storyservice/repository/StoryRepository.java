package feddericovonwernich.example.storyservice.repository;

import feddericovonwernich.example.storyservice.model.Story;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoryRepository extends JpaRepository<Story, Long> { }
