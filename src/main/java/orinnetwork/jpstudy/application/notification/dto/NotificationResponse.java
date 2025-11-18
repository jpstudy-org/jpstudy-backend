package orinnetwork.jpstudy.application.notification.dto;

import lombok.Getter;
import orinnetwork.jpstudy.domain.notification.Notification;
import orinnetwork.jpstudy.domain.notification.NotificationType;

@Getter
public class NotificationResponse {

    private final Long id;
    private final String content;
    private final NotificationType type;
    private final String relatedUrl;
    private final boolean isRead;

    public NotificationResponse(Notification notification) {
        this.id = notification.getId();
        this.content = notification.getContent();
        this.type = notification.getType();
        this.relatedUrl = notification.getRelatedUrl();
        this.isRead = notification.getIsRead();
    }
}
