package orinnetwork.jpstudy.domain.inquiry;

import io.jsonwebtoken.lang.Assert;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Inquiry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long memberId;

    private String title;

    @Lob
    private String content;

    @Enumerated(EnumType.STRING)
    private InquiryStatus status;

    private LocalDateTime createdAt;

    @Lob
    private String answerContent;

    private LocalDateTime answeredAt;

    @OneToMany(mappedBy = "inquiry", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InquiryAttachment> attachments = new ArrayList<>();

    public Inquiry(Long memberId, String title, String content) {
        Assert.hasText(title, "Title must not be empty");
        Assert.hasText(content, "Content must not be empty");

        this.memberId = memberId;
        this.title = title;
        this.content = content;
        this.status = InquiryStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    public static Inquiry create(Long memberId, String title, String content, List<InquiryAttachment> newAttachments) {
        if (newAttachments.size() > 3) {
            throw new IllegalArgumentException("Cannot attach more than 3 files.");
        }

        Inquiry inquiry = new Inquiry(memberId, title, content);

        newAttachments.forEach(inquiry::addAttachment);

        return inquiry;
    }

    private void addAttachment(InquiryAttachment attachment) {
        this.attachments.add(attachment);
        attachment.setInquiry(this);
    }

    public void answer(String answerContent) {
        this.answerContent = answerContent;
        this.answeredAt = LocalDateTime.now();
        this.status = InquiryStatus.ANSWERED;
    }
}
