package feddericovonwernich.example.storyservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class ExecutorConfig {

    @Value("${story.publish.executor.core-pool-size:4}")
    private int corePoolSize;

    @Value("${story.publish.executor.max-pool-size:16}")
    private int maxPoolSize;

    @Value("${story.publish.executor.queue-capacity:100}")
    private int queueCapacity;

    @Bean(name = "storyPublishExecutor")
    public Executor storyPublishExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);

        executor.setThreadNamePrefix("StoryPub-");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(30);
        executor.initialize();

        return executor;
    }

}
