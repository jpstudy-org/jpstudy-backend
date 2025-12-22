package orinnetwork.jpstudy.presentation.notification;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.MediaType;
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
import orinnetwork.jpstudy.infrastructure.security.CustomUserDetails;

@Tag(name = "Notification API", description = "실시간 알림(SSE) 및 알림 목록/상태 관리")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Operation(
            summary = "SSE 연결 구독",
            description = "인증된 사용자를 위한 서버-센트 이벤트(SSE) 구독 엔드포인트입니다. 연결 유지 시 서버에서 알림을 실시간으로 전송합니다. Content-Type: text/event-stream",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "SSE 연결 성공",
                            content = @Content(mediaType = "text/event-stream",
                                    schema = @Schema(implementation = SseEmitter.class)))
            }
    )
    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long memberId = userDetails.getMemberId();
        return notificationService.subscribe(memberId);
    }

    @Operation(summary = "모든 알림 읽음 처리", description = "현재 사용자의 모든 읽지 않은 알림을 읽음 상태로 변경합니다.")
    @PatchMapping("/notifications/read-all")
    public ResponseEntity<Void> readAllNotifications(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }

        Long currentUserId = userDetails.getMemberId();
        notificationService.markAllAsRead(currentUserId);

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "특정 알림 읽음 처리", description = "특정 ID의 알림을 읽음 상태로 변경합니다.")
    @PatchMapping("/notifications/{notificationId}/read")
    public ResponseEntity<Void> readNotification(
            @Parameter(description = "읽음 처리할 알림 ID", in = ParameterIn.PATH)
            @PathVariable Long notificationId,

            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        notificationService.markAsRead(notificationId, userDetails.getMemberId());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "읽지 않은 알림 목록 조회", description = "현재 사용자의 읽지 않은 모든 알림 목록을 반환합니다.")
    @GetMapping("/notifications")
    public ResponseEntity<List<NotificationResponse>> getNotifications(
            @Parameter(hidden = true)
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

    @Operation(summary = "읽지 않은 알림 개수 조회", description = "현재 사용자의 읽지 않은 알림의 개수를 반환합니다.")
    @GetMapping("/notifications/count")
    public ResponseEntity<Long> getUnreadCount(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }

        long count = notificationService.getUnreadCount(userDetails.getMemberId());
        return ResponseEntity.ok(count);
    }
}
