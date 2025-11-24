package orinnetwork.jpstudy.application.inquiry;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import orinnetwork.jpstudy.application.image.ImageService;
import orinnetwork.jpstudy.application.inquiry.dto.AttachmentRequest;
import orinnetwork.jpstudy.application.inquiry.dto.AttachmentResponse;
import orinnetwork.jpstudy.application.inquiry.dto.CreateInquiryRequest;
import orinnetwork.jpstudy.application.inquiry.dto.InquiryResponse;
import orinnetwork.jpstudy.domain.inquiry.Inquiry;
import orinnetwork.jpstudy.domain.inquiry.InquiryAttachment;
import orinnetwork.jpstudy.domain.inquiry.InquiryRepository;
import orinnetwork.jpstudy.infrastructure.exception.CustomException;
import orinnetwork.jpstudy.infrastructure.exception.ErrorCode;

@Service
@RequiredArgsConstructor
public class InquiryService {

    private final InquiryRepository inquiryRepository;
    private final ImageService imageService;

    /**
     * 문의하기
     */
    @Transactional
    public Long createInquiry(CreateInquiryRequest request, Long memberId) {
        List<InquiryAttachment> attachments = request.attachments().stream()
                .map(this::mapToAttachmentEntity)
                .toList();

        Inquiry inquiry = Inquiry.create(
                memberId,
                request.title(),
                request.content(),
                attachments
        );

        Inquiry savedInquiry = inquiryRepository.save(inquiry);

        return savedInquiry.getId();
    }

    @Transactional(readOnly = true)
    public InquiryResponse getInquiryDetails(Long inquiryId, Long memberId) {
        Inquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new CustomException(ErrorCode.INQUIRY_NOT_FOUND));

        validateOwnership(inquiry, memberId);

        List<AttachmentResponse> attachmentResponses = inquiry.getAttachments().stream()
                .map(this::toAttachmentResponse)
                .toList();

        return InquiryResponse.of(inquiry, attachmentResponses);
    }

    // --- Private ---

    private InquiryAttachment mapToAttachmentEntity(AttachmentRequest dto) {
        return InquiryAttachment.create(
                dto.storageKey(),
                dto.originalFileName(),
                dto.fileSize(),
                dto.contentType()
        );
    }

    private void validateOwnership(Inquiry inquiry, Long memberId) {
        if (!inquiry.getMemberId().equals(memberId)) {
            throw new CustomException(ErrorCode.INQUIRY_NOT_OWNER);
        }
    }

    private AttachmentResponse toAttachmentResponse(InquiryAttachment attachment) {
        String presignedUrl = imageService.getStartPresignedUrl(attachment.getStorageKey());
        return AttachmentResponse.of(attachment, presignedUrl);
    }
}
