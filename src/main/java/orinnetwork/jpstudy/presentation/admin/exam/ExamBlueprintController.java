package orinnetwork.jpstudy.presentation.admin.exam;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
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

@Tag(name = "Admin - Exam", description = "관리자: 시험 출제 양식(Blueprint) 관리")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/admin/exam/blueprints")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class ExamBlueprintController {

    private final ExamBlueprintService blueprintService;

    @Operation(summary = "출제 양식 생성", description = "새로운 시험 출제 양식(Blueprint)을 생성합니다. (문제 구성, 비율 등 설정)")
    @PostMapping
    public ResponseEntity<Long> createBlueprint(@RequestBody CreateBlueprintRequest request) {
        Long id = blueprintService.createBlueprint(request);
        return ResponseEntity.created(URI.create("/api/admin/blueprints/" + id)).body(id);
    }

    @Operation(summary = "출제 양식 목록 조회", description = "등록된 모든 출제 양식 목록을 페이지네이션하여 조회합니다.")
    @GetMapping
    public ResponseEntity<CustomPageResponse<BlueprintResponse>> getBlueprints(
            @ParameterObject
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(blueprintService.getAllBlueprints(pageable));
    }

    @Operation(summary = "출제 양식 단건 조회", description = "특정 ID의 출제 양식 상세 정보를 조회합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<BlueprintResponse> getBlueprint(
            @Parameter(description = "조회할 출제 양식 ID")
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(blueprintService.getBlueprint(id));
    }

    @Operation(summary = "출제 양식 수정", description = "특정 출제 양식의 내용을 수정합니다.")
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateBlueprint(
            @Parameter(description = "수정할 출제 양식 ID")
            @PathVariable Long id,

            @RequestBody CreateBlueprintRequest request
    ) {
        blueprintService.updateBlueprint(id, request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "출제 양식 삭제", description = "특정 출제 양식(Blueprint)을 삭제합니다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBlueprint(
            @Parameter(description = "삭제할 출제 양식 ID")
            @PathVariable Long id
    ) {
        blueprintService.deleteBlueprint(id);
        return ResponseEntity.noContent().build();
    }
}
