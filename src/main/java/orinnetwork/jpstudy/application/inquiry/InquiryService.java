package orinnetwork.jpstudy.application.inquiry;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import orinnetwork.jpstudy.application.inquiry.dto.AttachmentRequest;
import orinnetwork.jpstudy.application.inquiry.dto.CreateInquiryRequest;
import orinnetwork.jpstudy.application.inquiry.dto.InquiryResponse;
import orinnetwork.jpstudy.domain.inquiry.Inquiry;
import orinnetwork.jpstudy.domain.inquiry.InquiryAttachment;
import orinnetwork.jpstudy.domain.inquiry.InquiryRepository;

@Service
@RequiredArgsConstructor
public class InquiryService {

    private final InquiryRepository inquiryRepository;

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

    private InquiryAttachment mapToAttachmentEntity(AttachmentRequest dto) {
        return InquiryAttachment.create(
                dto.storageKey(),
                dto.originalFileName(),
                dto.fileSize(),
                dto.contentType()
        );
    }

    @Transactional(readOnly = true)
    public InquiryResponse getInquiryDetails(Long inquiryId, Long memberId) {
        Inquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new IllegalArgumentException("Inquiry not found: " + inquiryId));

        if (!inquiry.getMemberId().equals(memberId)) {
            throw new IllegalArgumentException("권한 없음");
        }

        return InquiryResponse.from(inquiry);
    }

}
