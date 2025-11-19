package orinnetwork.jpstudy.application.inquiry.dto;

import java.util.List;

public record CreateInquiryRequest(
        String title,
        String content,
        List<AttachmentRequest> attachments
) {
}