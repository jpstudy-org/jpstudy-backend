package orinnetwork.jpstudy.presentation.admin.inquiry;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
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

@Tag(name = "Admin - Inquiry", description = "관리자: 사용자 1:1 문의 조회 및 답변")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/admin/inquiries")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminInquiryController {

    private final AdminInquiryService adminInquiryService;

    @Operation(summary = "문의 답변 등록", description = "특정 문의 ID에 대한 답변을 작성 및 등록하고 문의 상태를 완료(COMPLETED)로 변경합니다.")
    @PostMapping("/{inquiryId}/answer")
    public ResponseEntity<Void> answerInquiry(
            @Parameter(description = "답변을 등록할 문의 ID")
            @PathVariable Long inquiryId,

            @RequestBody AnswerInquiryRequest request
    ) {
        adminInquiryService.answerInquiry(inquiryId, request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "문의 상세 조회", description = "특정 문의 ID의 상세 내용과 답변 정보를 조회합니다.")
    @GetMapping("/{inquiryId}")
    public ResponseEntity<InquiryResponse> getInquiry(
            @Parameter(description = "조회할 문의 ID")
            @PathVariable Long inquiryId
    ) {
        return ResponseEntity.ok(adminInquiryService.getInquiry(inquiryId));
    }

    @Operation(summary = "문의 목록 조회 및 검색", description = "문의 상태(status)별로 필터링하여 문의 목록을 페이지네이션하여 조회합니다.")
    @GetMapping
    public ResponseEntity<CustomPageResponse<InquiryAdminSummary>> getInquiries(
            @Parameter(description = "문의 상태 필터 (예: PENDING, COMPLETED)")
            @RequestParam(required = false) InquiryStatus status,

            @ParameterObject
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        CustomPageResponse<InquiryAdminSummary> response = adminInquiryService.getInquiries(status, pageable);
        return ResponseEntity.ok(response);
    }
}
