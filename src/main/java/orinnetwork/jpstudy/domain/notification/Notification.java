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

    @Builder
    public Notification(Long recipientId, String content, NotificationType type, String relatedUrl, Boolean isRead) {
        this.recipientId = recipientId;
        this.content = content;
        this.type = type;
        this.relatedUrl = relatedUrl;
        this.isRead = isRead != null ? isRead : false;
    }

    public void read() {
        this.isRead = true;
    }
}
