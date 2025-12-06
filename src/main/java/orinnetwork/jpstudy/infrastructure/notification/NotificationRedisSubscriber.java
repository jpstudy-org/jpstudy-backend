package orinnetwork.jpstudy.infrastructure.notification;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Component
@RequiredArgsConstructor
public class NotificationRedisSubscriber implements MessageListener {

    private final SseEmitterRepository sseEmitterRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String notificationContent = new String(message.getBody());

        String channel = new String(message.getChannel());
        Long userId = Long.valueOf(channel.split(":")[1]);

        SseEmitter emitter = sseEmitterRepository.get(userId);

        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .name("notification")
                        .data(notificationContent));
            } catch (Exception e) {
                sseEmitterRepository.deleteById(userId);
            }
        }
    }
}