package orinnetwork.jpstudy.domain.notification;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import orinnetwork.jpstudy.infrastructure.exception.CustomException;
import orinnetwork.jpstudy.infrastructure.exception.ErrorCode;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long recipientId;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private NotificationType type;

    @Column(nullable = false)
    private Boolean isRead = false;

    private String relatedUrl;

    @Builder(access = AccessLevel.PRIVATE)
    private Notification(Long recipientId, String content, NotificationType type, String relatedUrl, Boolean isRead) {
        this.recipientId = recipientId;
        this.content = content;
        this.type = type;
        this.relatedUrl = relatedUrl;
        this.isRead = isRead != null ? isRead : false;
    }

    public static Notification create(Long recipientId, NotificationType type, String content, String url) {
        if (type == NotificationType.INQUIRY && (url == null || url.isBlank())) {
            throw new CustomException(ErrorCode.INVALID_INPUT);
        }

        return Notification.builder()
                .recipientId(recipientId)
                .content(content)
                .type(type)
                .relatedUrl(url)
                .isRead(false)
                .build();
    }

    public void read() {
        this.isRead = true;
    }
}
