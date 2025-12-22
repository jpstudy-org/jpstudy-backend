package orinnetwork.jpstudy.presentation.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import orinnetwork.jpstudy.application.post.PostService;

@Component
@RequiredArgsConstructor
public class PostViewCountScheduler {

    private final PostService postService;

    @Scheduled(fixedDelay = 60000)
    public void syncViewCounts() {
        postService.syncViewCountsToDB();
    }
}
