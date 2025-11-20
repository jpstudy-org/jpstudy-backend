package orinnetwork.jpstudy.presentation.admin.inquiry;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import orinnetwork.jpstudy.application.admin.inquiry.AdminInquiryService;
import orinnetwork.jpstudy.application.admin.inquiry.dto.AnswerInquiryRequest;
import orinnetwork.jpstudy.application.admin.inquiry.dto.InquiryAdminSummary;
import orinnetwork.jpstudy.application.dto.CustomPageResponse;
import orinnetwork.jpstudy.application.inquiry.dto.InquiryResponse;
import orinnetwork.jpstudy.domain.inquiry.InquiryStatus;

@RestController
@RequestMapping("/api/admin/inquiries")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminInquiryController {

    private final AdminInquiryService adminInquiryService;

    @PostMapping("/{inquiryId}/answer")
    public ResponseEntity<Void> answerInquiry(
            @PathVariable Long inquiryId,
            @RequestBody AnswerInquiryRequest request
    ) {
        adminInquiryService.answerInquiry(inquiryId, request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{inquiryId}")
    public ResponseEntity<InquiryResponse> getInquiry(@PathVariable Long inquiryId) {
        return ResponseEntity.ok(adminInquiryService.getInquiry(inquiryId));
    }

    @GetMapping
    public ResponseEntity<CustomPageResponse<InquiryAdminSummary>> getInquiries(
            @RequestParam(required = false) InquiryStatus status,
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        CustomPageResponse<InquiryAdminSummary> response = adminInquiryService.getInquiries(status, pageable);
        return ResponseEntity.ok(response);
    }
}
