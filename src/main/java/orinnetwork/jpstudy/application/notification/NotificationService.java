package orinnetwork.jpstudy.application.notification;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import orinnetwork.jpstudy.application.notification.dto.NotificationResponse;
import orinnetwork.jpstudy.domain.notification.Notification;
import orinnetwork.jpstudy.domain.notification.NotificationMessage;
import orinnetwork.jpstudy.domain.notification.NotificationRepository;
import orinnetwork.jpstudy.domain.notification.NotificationType;
import orinnetwork.jpstudy.infrastructure.exception.CustomException;
import orinnetwork.jpstudy.infrastructure.exception.ErrorCode;
import orinnetwork.jpstudy.infrastructure.notification.SseEmitterRepository;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final SseEmitterRepository sseEmitterRepository;
    private final RedisTemplate<String, Object> notificationRedisTemplate;
    private final MessageSource messageSource;

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
    public void send(Long recipientId, NotificationType type, NotificationMessage messageCode, String languageCode,
                     String url, Object... args) {

        Locale locale = Locale.ENGLISH;
        if ("ko".equalsIgnoreCase(languageCode)) {
            locale = Locale.KOREA;
        }

        String translatedContent = messageSource.getMessage(messageCode.getKey(), args, locale);

        Notification notification = Notification.builder()
                .recipientId(recipientId)
                .content(translatedContent)
                .type(type)
                .relatedUrl(url)
                .isRead(false)
                .build();

        notificationRepository.save(notification);

        String channelName = "user-channel:" + recipientId;
        notificationRedisTemplate.convertAndSend(channelName, translatedContent);
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
     *
     * @param recipientId 사용자 ID
     * @return 알림 개수
     */
    @Transactional(readOnly = true)
    public long getUnreadCount(Long recipientId) {
        return notificationRepository.countByRecipientIdAndIsReadFalse(recipientId);
    }

    /**
     * 전체 읽음 처리
     *
     * @param recipientId 사용자 ID
     */
    @Transactional
    public void markAllAsRead(Long recipientId) {
        notificationRepository.markAllAsReadForUser(recipientId);
    }

    /**
     * 선택 읽음 처리
     *
     * @param notificationId 알림 ID
     * @param memberId       사용자 ID
     */
    @Transactional
    public void markAsRead(Long notificationId, Long memberId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOTIFICATION_NOT_FOUND));

        if (!notification.getRecipientId().equals(memberId)) {
            throw new CustomException(ErrorCode.NOTIFICATION_NOT_OWNER);
        }

        notification.read();
    }
}