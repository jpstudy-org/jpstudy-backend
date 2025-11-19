package orinnetwork.jpstudy.domain.inquiry;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InquiryAttachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inquiry_id")
    private Inquiry inquiry;

    private String storageKey;
    private String originalFileName;
    private long fileSize;
    private String contentType;

    private InquiryAttachment(String storageKey, String originalFileName, long fileSize, String contentType) {
        this.storageKey = storageKey;
        this.originalFileName = originalFileName;
        this.fileSize = fileSize;
        this.contentType = contentType;
    }

    public static InquiryAttachment create(String storageKey, String originalFileName, long fileSize,
                                           String contentType) {
        return new InquiryAttachment(storageKey, originalFileName, fileSize, contentType);
    }

    protected void setInquiry(Inquiry inquiry) {
        this.inquiry = inquiry;
    }
}
