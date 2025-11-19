package orinnetwork.jpstudy.presentation.admin.exam;

import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import orinnetwork.jpstudy.application.admin.exam.ExamBlueprintService;
import orinnetwork.jpstudy.application.admin.exam.dto.BlueprintResponse;
import orinnetwork.jpstudy.application.admin.exam.dto.CreateBlueprintRequest;
import orinnetwork.jpstudy.application.dto.CustomPageResponse;

@RestController
@RequestMapping("/api/admin/exam/blueprints")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class ExamBlueprintController {

    private final ExamBlueprintService blueprintService;

    @PostMapping
    public ResponseEntity<Long> createBlueprint(@RequestBody CreateBlueprintRequest request) {
        Long id = blueprintService.createBlueprint(request);
        return ResponseEntity.created(URI.create("/api/admin/blueprints/" + id)).body(id);
    }

    // 목록 조회
    @GetMapping
    public ResponseEntity<CustomPageResponse<BlueprintResponse>> getBlueprints(
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(blueprintService.getAllBlueprints(pageable));
    }

    // 단건 조회
    @GetMapping("/{id}")
    public ResponseEntity<BlueprintResponse> getBlueprint(@PathVariable Long id) {
        return ResponseEntity.ok(blueprintService.getBlueprint(id));
    }

    // 수정
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateBlueprint(
            @PathVariable Long id,
            @RequestBody CreateBlueprintRequest request
    ) {
        blueprintService.updateBlueprint(id, request);
        return ResponseEntity.ok().build();
    }

    // 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBlueprint(@PathVariable Long id) {
        blueprintService.deleteBlueprint(id);
        return ResponseEntity.noContent().build();
    }
}
