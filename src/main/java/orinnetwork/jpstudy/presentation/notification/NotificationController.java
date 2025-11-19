package orinnetwork.jpstudy.presentation.notification;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import orinnetwork.jpstudy.application.notification.NotificationService;
import orinnetwork.jpstudy.application.notification.dto.NotificationResponse;
import orinnetwork.jpstudy.domain.notification.Notification;
import orinnetwork.jpstudy.infrastructure.security.CustomUserDetails;

@RestController
@RequestMapping("/api")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping(value = "/subscribe", produces = "text/event-stream")
    public SseEmitter subscribe(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long memberId = userDetails.getMemberId();
        return notificationService.subscribe(memberId);
    }

    @PatchMapping("/notifications/read-all")
    public ResponseEntity<Void> readAllNotifications(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }

        Long currentUserId = userDetails.getMemberId();
        notificationService.markAllAsRead(currentUserId);

        return ResponseEntity.ok().build();
    }

    @PatchMapping("/notifications/{notificationId}/read")
    public ResponseEntity<Void> readNotification(
            @PathVariable Long notificationId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        notificationService.markAsRead(notificationId, userDetails.getMemberId());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/notifications")
    public ResponseEntity<List<NotificationResponse>> getNotifications(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }

        List<NotificationResponse> notifications = notificationService.getUnreadNotifications(
                userDetails.getMemberId()
        );

        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/notifications/count")
    public ResponseEntity<Long> getUnreadCount(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }

        long count = notificationService.getUnreadCount(userDetails.getMemberId());
        return ResponseEntity.ok(count);
    }
}
