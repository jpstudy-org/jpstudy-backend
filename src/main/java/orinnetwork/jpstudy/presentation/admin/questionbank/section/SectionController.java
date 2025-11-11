package orinnetwork.jpstudy.presentation.admin.questionbank.section;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import orinnetwork.jpstudy.application.admin.questionbank.section.SectionService;
import orinnetwork.jpstudy.application.admin.questionbank.section.dto.SectionResponse;

@RestController
@RequestMapping("/api/admin/questionbank/sections")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class SectionController {

    private final SectionService sectionService;

    @GetMapping
    public ResponseEntity<List<SectionResponse>> getAllSections() {
        List<SectionResponse> sections = sectionService.getAllSections();
        return ResponseEntity.ok(sections);
    }
}
