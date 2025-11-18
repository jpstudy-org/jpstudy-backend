package orinnetwork.jpstudy.application.notification;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import orinnetwork.jpstudy.application.notification.dto.NotificationResponse;
import orinnetwork.jpstudy.domain.notification.Notification;
import orinnetwork.jpstudy.domain.notification.NotificationRepository;
import orinnetwork.jpstudy.domain.notification.NotificationType;
import orinnetwork.jpstudy.infrastructure.notification.SseEmitterRepository;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final SseEmitterRepository sseEmitterRepository;
    private final RedisTemplate<String, Object> notificationRedisTemplate;

    public SseEmitter subscribe(Long userId) {
        SseEmitter emitter = new SseEmitter(1000L * 60 * 10);

        sseEmitterRepository.save(userId, emitter);

        emitter.onCompletion(() -> sseEmitterRepository.deleteById(userId));
        emitter.onTimeout(() -> sseEmitterRepository.deleteById(userId));

        try {
            emitter.send(SseEmitter.event()
                    .name("connect")
                    .data("Connected to SSE"));
        } catch (IOException e) {
            emitter.completeWithError(e);
            sseEmitterRepository.deleteById(userId);
        }

        return emitter;
    }

    @Transactional
    public void send(Long recipientId, NotificationType type, String content, String url) {

        Notification notification = Notification.builder()
                .recipientId(recipientId)
                .content(content)
                .type(type)
                .relatedUrl(url)
                .isRead(false)
                .build();

        notificationRepository.save(notification);

        String channelName = "user-channel:" + recipientId;

        notificationRedisTemplate.convertAndSend(channelName, content);
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> getUnreadNotifications(Long recipientId) {
        List<Notification> notifications = notificationRepository
                .findByRecipientIdAndIsReadFalseOrderByIdDesc(recipientId);

        return notifications.stream()
                .map(NotificationResponse::new)
                .toList();
    }

    /**
     * 안 읽은 알림 개수 조회
     * @param recipientId 사용자 ID
     * @return 알림 개수
     */
    @Transactional(readOnly = true)
    public long getUnreadCound(Long recipientId) {
        return notificationRepository.countByRecipientIdAndIsReadFalse(recipientId);
    }

    /**
     * 전체 읽음 처리
     * @param recipientId 사용자 ID
     */
    @Transactional
    public void markAllAsRead(Long recipientId) {
        notificationRepository.markAllAsReadForUser(recipientId);
    }

    /**
     * 선택 읽음 처리
     * @param notificationId 알림 ID
     * @param memberId 사용자 ID
     */
    @Transactional
    public void markAsRead(Long notificationId, Long memberId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found"));

        if (!notification.getRecipientId().equals(memberId)) {
            throw new SecurityException("Not authorized to read this notification");
        }

        notification.read();
    }
}