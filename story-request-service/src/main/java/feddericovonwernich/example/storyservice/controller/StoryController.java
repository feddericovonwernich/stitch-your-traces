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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@RestController
@RequestMapping("/stories")
@Tag(name = "Stories", description = "API for managing stories")
public class StoryController {

    private final StoryService storyService;

    public StoryController(StoryService storyService) {
        this.storyService = storyService;
    }

    @GetMapping
    @Operation(summary = "Get stories", description = "Get stories – paginated")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved stories",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = StoryResponse.class)))
    })
    public Page<StoryResponse> getStories(
            @Parameter(description = "Page number to retrieve", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page", example = "10")
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

    @PostMapping
    @Operation(summary = "Request story creation", description = "Request human or AI story creation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Story request accepted",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = StoryRequestResponse.class)))
    })
    public StoryRequestResponse requestStory(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Details for story creation", required = true,
                    content = @Content(schema = @Schema(implementation = CreateStoryRequest.class)))
            @RequestBody CreateStoryRequest body) {
        var req = storyService.createRequest(body);
        List<Long> ids = storyService.storyIds(req);
        return new StoryRequestResponse(req.getId(), req.getStatus(), ids);
    }

    @GetMapping("/{id}/status")
    @Operation(summary = "Check story request status", description = "Check the creation status of a story request")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved request status",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = StoryRequestResponse.class)))
    })
    public StoryRequestResponse getStatus(
            @Parameter(description = "ID of the story request to check", example = "1")
            @PathVariable("id") Long id) {
        var req = storyService.getRequest(id);
        List<Long> ids = storyService.storyIds(req);
        return new StoryRequestResponse(req.getId(), req.getStatus(), ids);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Save or attach a new story", description = "Save/attach a new story (AI worker or additional human draft)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Story saved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Story.class)))
    })
    public Story saveStory(
            @Parameter(description = "ID of the story request to attach the story to", example = "1")
            @PathVariable("id") Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Title and content of the story to be saved", required = true,
                    content = @Content(schema = @Schema(implementation = SaveStoryRequest.class)))
            @RequestBody SaveStoryRequest saveStoryRequest) {
        return storyService.saveStoryForRequest(id, saveStoryRequest.title(), saveStoryRequest.content());
    }

}
