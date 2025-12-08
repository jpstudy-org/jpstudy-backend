package orinnetwork.jpstudy.application.notification.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import orinnetwork.jpstudy.domain.notification.Notification;
import orinnetwork.jpstudy.domain.notification.NotificationType;

@Getter
@Schema(description = "사용자 알림 (Notification) 응답 DTO")
public class NotificationResponse {

    @Schema(description = "알림의 고유 ID")
    private final Long id;

    @Schema(description = "알림 내용 (사용자에게 표시될 텍스트)")
    private final String content;

    @Schema(description = "알림의 유형 (예: POST_NEW_COMMENT, ADMIN_NOTICE, EXAM_RESULT 등)")
    private final NotificationType type;

    @Schema(description = "알림 클릭 시 이동할 관련 페이지 URL 또는 경로 (선택 사항)", nullable = true)
    private final String relatedUrl;

    @Schema(description = "알림을 사용자가 읽었는지 여부")
    private final boolean isRead;

    public NotificationResponse(Notification notification) {
        this.id = notification.getId();
        this.content = notification.getContent();
        this.type = notification.getType();
        this.relatedUrl = notification.getRelatedUrl();
        this.isRead = notification.getIsRead();
    }
}
