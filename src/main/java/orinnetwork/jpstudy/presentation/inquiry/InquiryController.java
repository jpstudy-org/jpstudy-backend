package orinnetwork.jpstudy.presentation.inquiry;

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

@RestController
@RequestMapping("/api/inquiries")
@RequiredArgsConstructor
public class InquiryController {

    private final InquiryService inquiryService;

    @PostMapping
    public ResponseEntity<Void> createInquiry(
            @RequestBody CreateInquiryRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long memberId = userDetails.getMemberId();
        Long inquiryId = inquiryService.createInquiry(request, memberId);

        URI location = URI.create("/api/inquiries/" + inquiryId);
        return ResponseEntity.created(location).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<InquiryResponse> getInquiry(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long memberId = userDetails.getMemberId();

        InquiryResponse inquiryResponse = inquiryService.getInquiryDetails(id, memberId);

        return ResponseEntity.ok(inquiryResponse);
    }
}