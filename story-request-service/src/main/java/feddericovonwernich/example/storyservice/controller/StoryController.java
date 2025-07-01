package feddericovonwernich.example.storyservice.controller;

import feddericovonwernich.example.storyservice.dto.CreateStoryRequest;
import feddericovonwernich.example.storyservice.dto.SaveStoryRequest;
import feddericovonwernich.example.storyservice.dto.StoryRequestResponse;
import feddericovonwernich.example.storyservice.dto.StoryResponse;
import feddericovonwernich.example.storyservice.model.Story;
import feddericovonwernich.example.storyservice.service.StoryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/stories")
public class StoryController {

    private final StoryService storyService;

    public StoryController(StoryService storyService) {
        this.storyService = storyService;
    }

    /**
     * Get stories – paginated.
     *
     * @param page the page number to retrieve, default is 0
     * @param size the number of items per page, default is 10
     * @return a paginated list of stories
     */
    @GetMapping
    public Page<StoryResponse> getStories(@RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "10") int size) {
        return storyService.getAllStories(PageRequest.of(page, size))
           .map(story -> new StoryResponse(
                   story.getId(),
                   story.getTitle(),
                   story.getContent(),
                   story.getSource(),
                   story.getCreatedAt(),
                   story.getUpdatedAt())
           );
    }

    /**
     * Request story creation (human or AI).
     *
     * @param body the request body containing details for story creation
     * @return a response containing the request ID, status, and associated story IDs
     */
    @PostMapping
    public StoryRequestResponse requestStory(@RequestBody CreateStoryRequest body) {
        var req = storyService.createRequest(body);
        List<Long> ids = storyService.storyIds(req);
        return new StoryRequestResponse(req.getId(), req.getStatus(), ids);
    }

    /**
     * Check the creation status of a story request.
     *
     * @param id the ID of the story request
     * @return a response containing the request ID, status, and associated story IDs
     */
    @GetMapping("/{id}/status")
    public StoryRequestResponse getStatus(@PathVariable("id") Long id) {
        var req = storyService.getRequest(id);
        List<Long> ids = storyService.storyIds(req);
        return new StoryRequestResponse(req.getId(), req.getStatus(), ids);
    }

    /**
     * Save/attach a new story (AI worker or additional human draft).
     *
     * @param id            the ID of the story request to attach the story to
     * @param saveStoryRequest the request body containing the title and content of the story to be saved
     * @return the saved story
     */
    @PutMapping("/{id}")
    public Story saveStory(@PathVariable("id") Long id, @RequestBody SaveStoryRequest saveStoryRequest) {
        return storyService.saveStoryForRequest(id, saveStoryRequest.title(), saveStoryRequest.content());
    }

}
