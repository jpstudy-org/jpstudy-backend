package orinnetwork.jpstudy.application.notification.component;

import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import orinnetwork.jpstudy.infrastructure.notification.SseEmitterRepository;

@Component
@RequiredArgsConstructor
public class SseConnector {

    private final SseEmitterRepository sseEmitterRepository;
    private static final Long DEFAULT_TIMEOUT = 1000L * 60 * 10; // 10분

    public SseEmitter connect(Long userId) {
        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);
        sseEmitterRepository.save(userId, emitter);

        setupLifecycle(emitter, userId);
        sendInitialEvent(emitter, userId);

        return emitter;
    }

    private void setupLifecycle(SseEmitter emitter, Long userId) {
        emitter.onCompletion(() -> sseEmitterRepository.deleteById(userId));
        emitter.onTimeout(() -> sseEmitterRepository.deleteById(userId));
    }

    private void sendInitialEvent(SseEmitter emitter, Long userId) {
        try {
            emitter.send(SseEmitter.event()
                    .name("connect")
                    .data("Connected to SSE"));
        } catch (IOException e) {
            emitter.completeWithError(e);
            sseEmitterRepository.deleteById(userId);
        }
    }
}
