package orinnetwork.jpstudy.application.admin.inquiry;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import orinnetwork.jpstudy.application.admin.inquiry.dto.AnswerInquiryRequest;
import orinnetwork.jpstudy.application.admin.inquiry.dto.InquiryAdminSummary;
import orinnetwork.jpstudy.application.dto.CustomPageResponse;
import orinnetwork.jpstudy.application.image.ImageService;
import orinnetwork.jpstudy.application.inquiry.dto.AttachmentResponse;
import orinnetwork.jpstudy.application.inquiry.dto.InquiryResponse;
import orinnetwork.jpstudy.application.notification.NotificationService;
import orinnetwork.jpstudy.domain.inquiry.Inquiry;
import orinnetwork.jpstudy.domain.inquiry.InquiryRepository;
import orinnetwork.jpstudy.domain.inquiry.InquiryStatus;
import orinnetwork.jpstudy.domain.member.Member;
import orinnetwork.jpstudy.domain.member.MemberRepository;
import orinnetwork.jpstudy.domain.notification.NotificationMessage;
import orinnetwork.jpstudy.domain.notification.NotificationType;
import orinnetwork.jpstudy.infrastructure.exception.CustomException;
import orinnetwork.jpstudy.infrastructure.exception.ErrorCode;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminInquiryService {

    private final InquiryRepository inquiryRepository;
    private final NotificationService notificationService;
    private final MemberRepository memberRepository;
    private final ImageService imageService;

    public void answerInquiry(Long inquiryId, AnswerInquiryRequest request) {
        Inquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new CustomException(ErrorCode.INQUIRY_NOT_FOUND));

        inquiry.answer(request.content());

        Member member = memberRepository.findById(inquiry.getMemberId())
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        notificationService.send(
                member.getId(),
                NotificationType.INQUIRY,
                NotificationMessage.INQUIRY_ANSWERED,
                member.getLanguagePreference(),
                "/inquiries/" + inquiryId
        );
    }

    @Transactional(readOnly = true)
    public CustomPageResponse<InquiryAdminSummary> getInquiries(InquiryStatus status, Pageable pageable) {

        Page<Inquiry> inquiryPage;
        if (status != null) {
            inquiryPage = inquiryRepository.findByStatus(status, pageable);
        } else {
            inquiryPage = inquiryRepository.findAll(pageable);
        }

        Set<Long> memberIds = inquiryPage.getContent().stream()
                .map(Inquiry::getMemberId)
                .collect(Collectors.toSet());

        Map<Long, Member> memberMap = memberRepository.findAllById(memberIds).stream()
                .collect(Collectors.toMap(Member::getId, Function.identity()));

        Page<InquiryAdminSummary> responsePage = inquiryPage.map(inquiry -> {
            Member member = memberMap.get(inquiry.getMemberId());
            return InquiryAdminSummary.of(inquiry, member);
        });

        return new CustomPageResponse<>(responsePage);
    }

    @Transactional(readOnly = true)
    public InquiryResponse getInquiry(Long inquiryId) {
        Inquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new CustomException(ErrorCode.INQUIRY_NOT_FOUND));

        List<AttachmentResponse> attachmentResponses = inquiry.getAttachments().stream()
                .map(attachment -> {
                    String presignedUrl = imageService.getStartPresignedUrl(attachment.getStorageKey());
                    return AttachmentResponse.of(attachment, presignedUrl);
                })
                .toList();

        return InquiryResponse.of(inquiry, attachmentResponses);
    }
}
