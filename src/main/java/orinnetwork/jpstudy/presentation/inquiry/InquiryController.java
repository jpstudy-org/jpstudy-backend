package orinnetwork.jpstudy.presentation.inquiry;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import orinnetwork.jpstudy.application.inquiry.InquiryService;
import orinnetwork.jpstudy.application.inquiry.dto.CreateInquiryRequest;
import orinnetwork.jpstudy.application.inquiry.dto.InquiryResponse;
import orinnetwork.jpstudy.infrastructure.security.CustomUserDetails;

@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Inquiry API", description = "사용자 1:1 문의(Inquiry) 관리")
@RestController
@RequestMapping("/api/inquiries")
@RequiredArgsConstructor
public class InquiryController {

    private final InquiryService inquiryService;

    @Operation(summary = "새 문의 생성", description = "사용자(로그인된 멤버)가 1:1 문의를 등록합니다.")
    @PostMapping
    public ResponseEntity<Void> createInquiry(
            @RequestBody CreateInquiryRequest request,

            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long memberId = userDetails.getMemberId();
        Long inquiryId = inquiryService.createInquiry(request, memberId);

        URI location = URI.create("/api/inquiries/" + inquiryId);
        return ResponseEntity.created(location).build();
    }

    @Operation(summary = "문의 상세 조회", description = "특정 ID의 문의 내역을 조회합니다. 해당 문의의 작성자만 접근 가능합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<InquiryResponse> getInquiry(
            @Parameter(description = "조회할 문의 ID")
            @PathVariable Long id,

            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long memberId = userDetails.getMemberId();

        InquiryResponse inquiryResponse = inquiryService.getInquiryDetails(id, memberId);

        return ResponseEntity.ok(inquiryResponse);
    }
}