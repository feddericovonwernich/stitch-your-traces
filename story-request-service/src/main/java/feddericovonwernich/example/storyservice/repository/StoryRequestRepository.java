package feddericovonwernich.example.storyservice.repository;

import feddericovonwernich.example.storyservice.model.StoryRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoryRequestRepository extends JpaRepository<StoryRequest, Long> { }
