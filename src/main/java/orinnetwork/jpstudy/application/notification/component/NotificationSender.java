package orinnetwork.jpstudy.application.notification.component;

import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import orinnetwork.jpstudy.domain.notification.Notification;
import orinnetwork.jpstudy.domain.notification.NotificationMessage;
import orinnetwork.jpstudy.domain.notification.NotificationRepository;
import orinnetwork.jpstudy.domain.notification.NotificationType;

@Component
@RequiredArgsConstructor
public class NotificationSender {

    private final NotificationRepository notificationRepository;
    private final RedisTemplate<String, Object> notificationRedisTemplate;
    private final MessageSource messageSource;

    @Transactional
    public void send(Long recipientId, NotificationType type, NotificationMessage messageCode,
                     String languageCode, String url, Object... args) {

        String content = resolveMessage(messageCode, languageCode, args);

        saveNotification(recipientId, type, content, url);
        publishToRedis(recipientId, content);
    }

    private String resolveMessage(NotificationMessage code, String lang, Object... args) {
        Locale locale = "ko".equalsIgnoreCase(lang) ? Locale.KOREA : Locale.ENGLISH;
        return messageSource.getMessage(code.getKey(), args, locale);
    }

    private void saveNotification(Long recipientId, NotificationType type, String content, String url) {
        Notification notification = Notification.builder()
                .recipientId(recipientId)
                .content(content)
                .type(type)
                .relatedUrl(url)
                .isRead(false)
                .build();
        notificationRepository.save(notification);
    }

    private void publishToRedis(Long recipientId, String content) {
        String channelName = "user-channel:" + recipientId;
        notificationRedisTemplate.convertAndSend(channelName, content);
    }
}
