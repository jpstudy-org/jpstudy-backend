package orinnetwork.jpstudy.presentation.admin.questionbank.section;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import orinnetwork.jpstudy.application.admin.questionbank.section.SectionService;
import orinnetwork.jpstudy.application.admin.questionbank.section.dto.SectionResponse;

@Tag(name = "Admin - Question Bank", description = "관리자: 문제 은행 섹션 관리")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/admin/questionbank/sections")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class SectionController {

    private final SectionService sectionService;

    @Operation(
            summary = "모든 섹션 목록 조회 (관리자 전용)",
            description = "문제 은행에 등록된 모든 섹션(예: 파트) 목록을 조회합니다."
    )
    @GetMapping
    public ResponseEntity<List<SectionResponse>> getAllSections() {
        List<SectionResponse> sections = sectionService.getAllSections();
        return ResponseEntity.ok(sections);
    }
}
