package orinnetwork.jpstudy.application.notification;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import orinnetwork.jpstudy.application.notification.component.NotificationSender;
import orinnetwork.jpstudy.application.notification.component.SseConnector;
import orinnetwork.jpstudy.application.notification.dto.NotificationResponse;
import orinnetwork.jpstudy.domain.notification.Notification;
import orinnetwork.jpstudy.domain.notification.NotificationMessage;
import orinnetwork.jpstudy.domain.notification.NotificationRepository;
import orinnetwork.jpstudy.domain.notification.NotificationType;
import orinnetwork.jpstudy.infrastructure.exception.CustomException;
import orinnetwork.jpstudy.infrastructure.exception.ErrorCode;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final SseConnector sseConnector;
    private final NotificationSender notificationSender;

    /**
     * SSE 구독
     */
    public SseEmitter subscribe(Long userId) {
        return sseConnector.connect(userId);
    }

    /**
     * 알림 발송
     */
    @Transactional
    public void send(Long recipientId, NotificationType type, NotificationMessage messageCode, String languageCode,
                     String url, Object... args) {

        notificationSender.send(recipientId, type, messageCode, languageCode, url, args);
    }

    /**
     * 읽지 않은 알람 가져오기
     */
    @Transactional(readOnly = true)
    public List<NotificationResponse> getUnreadNotifications(Long recipientId) {
        return notificationRepository.findByRecipientIdAndIsReadFalseOrderByIdDesc(recipientId)
                .stream()
                .map(NotificationResponse::new)
                .toList();
    }

    /**
     * 단일 읽음 처리
     *
     * @param notificationId 알림 ID
     * @param memberId       사용자 ID
     */
    @Transactional
    public void markAsRead(Long notificationId, Long memberId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOTIFICATION_NOT_FOUND));

        validateOwnership(notification, memberId);

        notification.read();
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
     * 안 읽은 알림 개수 조회
     *
     * @param recipientId 사용자 ID
     * @return 알림 개수
     */
    @Transactional(readOnly = true)
    public long getUnreadCount(Long recipientId) {
        return notificationRepository.countByRecipientIdAndIsReadFalse(recipientId);
    }




    // --- Private ---
    private void validateOwnership(Notification notification, Long memberId) {
        if (!notification.getRecipientId().equals(memberId)) {
            throw new CustomException(ErrorCode.NOTIFICATION_NOT_OWNER);
        }
    }
}