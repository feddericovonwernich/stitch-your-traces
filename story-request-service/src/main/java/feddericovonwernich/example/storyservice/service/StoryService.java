package feddericovonwernich.example.storyservice.service;

import feddericovonwernich.example.storyservice.dto.CreateStoryRequest;
import feddericovonwernich.example.storyservice.model.Story;
import feddericovonwernich.example.storyservice.model.StoryRequest;
import feddericovonwernich.example.storyservice.model.StorySource;
import feddericovonwernich.example.storyservice.model.StoryStatus;
import feddericovonwernich.example.storyservice.repository.StoryRepository;
import feddericovonwernich.example.storyservice.repository.StoryRequestRepository;
import jakarta.annotation.PostConstruct;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static org.slf4j.LoggerFactory.getLogger;

@Service
public class StoryService {

    private static final Logger logger = getLogger(StoryService.class);

    @Value("${kafka.publish-topic:story-requests}")
    private String publishTopic;

    private final StoryRepository storyRepository;
    private final StoryRequestRepository requestRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${kafka.bootstrap-servers}")
    private String bootstrapServers;

    public StoryService(StoryRepository storyRepository,
                        StoryRequestRepository requestRepository,
                        KafkaTemplate<String, String> kafkaTemplate) {
        this.storyRepository = storyRepository;
        this.requestRepository = requestRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @PostConstruct
    public void createTopics() {
        var configs = Map.<String, Object>of(
                AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers
        );

        try (AdminClient admin = AdminClient.create(configs)) {

            // Get existing topics
            var existingTopics = admin.listTopics().names().get();

            // Only create it if it doesn't exist.
            if (!existingTopics.contains(publishTopic)) {
                NewTopic storyRequestTopic = new NewTopic(publishTopic, 2, (short) 1);
                // Blocks until created
                admin.createTopics(Collections.singletonList(storyRequestTopic)).all().get();
                logger.info("Created topic in Kafka {}", publishTopic);
            }


        } catch (ExecutionException | InterruptedException e) {
            logger.error("Error creating Kafka topics: {}", e.getMessage());
            throw new RuntimeException("Failed to create Kafka topics", e);
        }
    }


    public Page<Story> getAllStories(Pageable pageable) {
        return storyRepository.findAll(pageable);
    }

    public StoryRequest getRequest(Long id) {
        return requestRepository.findById(id).orElseThrow();
    }


    @Transactional
    public StoryRequest createRequest(CreateStoryRequest dto) {

        StoryRequest req = new StoryRequest();
        req.setTitle(dto.title());
        req.setContent(dto.content());
        req.setSource(dto.source());
        req.setStatus(StoryStatus.PENDING);

        // Save
        req = requestRepository.save(req);

        if (dto.source() == StorySource.AI) {
            // Publication happens in background using a separate thread.
            StoryRequest finalReq = req;

            new Thread(() -> {
                String messageContent = """
                        {
                            "title": "%s",
                            "content": "%s",
                            "id": %d
                        }
                    """.formatted(finalReq.getTitle(), finalReq.getContent(), finalReq.getId());

                kafkaTemplate.send(publishTopic, messageContent);
            }).start();
        }

        return req;
    }

    @Transactional
    public Story saveStoryForRequest(Long requestId, String title, String content) {
        StoryRequest req = getRequest(requestId);

        Story story = new Story();
        story.setTitle(title);
        story.setContent(content);
        story.setSource(req.getSource());
        story.setRequest(req);

        // Save the story
        storyRepository.save(story);

        // Update the request status.
        if (req.getStatus() == StoryStatus.PENDING) {
            req.setStatus(StoryStatus.COMPLETE);
        }

        requestRepository.save(req);

        return story;
    }

    public List<Long> storyIds(StoryRequest req) {
        return req.getStories().stream().map(Story::getId).toList();
    }
}
