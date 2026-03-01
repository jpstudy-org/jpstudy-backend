package orinnetwork.jpstudy.presentation.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import orinnetwork.jpstudy.application.anime.AnimeService;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "anime.sync.enabled", havingValue = "true", matchIfMissing = true)
public class AnimeSyncScheduler {

    private final AnimeService animeService;

    @Async
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        log.info("Application ready - triggering initial anime sync in background...");
        animeService.syncTrending();
        animeService.syncSeasonal();
    }

    @Scheduled(fixedDelay = 6 * 60 * 60 * 1000) // every 6 hours
    public void syncTrending() {
        animeService.syncTrending();
    }

    @Scheduled(fixedDelay = 12 * 60 * 60 * 1000) // every 12 hours
    public void syncSeasonal() {
        animeService.syncSeasonal();
    }
}
