package orinnetwork.jpstudy.presentation.admin.questionbank.level;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
import orinnetwork.jpstudy.application.admin.questionbank.level.LevelService;
import orinnetwork.jpstudy.application.admin.questionbank.level.dto.LevelRequest;
import orinnetwork.jpstudy.application.admin.questionbank.level.dto.LevelResponse;

@Tag(name = "Admin - Question Bank", description = "관리자: 문제 은행 레벨(등급) 관리")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/admin/questionbank/levels")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class LevelController {

    private final LevelService levelService;

    @Operation(summary = "새 레벨 생성", description = "새로운 문제 난이도(Level)를 생성합니다.")
    @PostMapping
    public ResponseEntity<LevelResponse> createLevel(@RequestBody LevelRequest request) {
        LevelResponse createdLevel = levelService.createLevel(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdLevel);
    }

    @Operation(summary = "레벨 정보 수정", description = "기존 레벨의 정보를 수정합니다.")
    @PutMapping("/{levelId}")
    public ResponseEntity<LevelResponse> updateLevel(
            @Parameter(description = "수정할 레벨 ID")
            @PathVariable Long levelId,

            @RequestBody LevelRequest request) {
        LevelResponse updatedLevel = levelService.updateLevel(levelId, request);
        return ResponseEntity.ok(updatedLevel);
    }

    @Operation(summary = "레벨 삭제", description = "특정 레벨을 삭제합니다.")
    @DeleteMapping("/{levelId}")
    public ResponseEntity<Void> deleteLevel(
            @Parameter(description = "삭제할 레벨 ID")
            @PathVariable Long levelId
    ) {
        levelService.deleteLevel(levelId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "레벨 단건 조회", description = "특정 레벨의 상세 정보를 조회합니다.")
    @GetMapping("/{levelId}")
    public ResponseEntity<LevelResponse> getLevel(
            @Parameter(description = "조회할 레벨 ID")
            @PathVariable Long levelId
    ) {
        LevelResponse level = levelService.getLevel(levelId);
        return ResponseEntity.ok(level);
    }

    @Operation(summary = "모든 레벨 목록 조회", description = "등록된 모든 레벨 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<List<LevelResponse>> getAllLevels() {
        List<LevelResponse> levels = levelService.getAllLevels();
        return ResponseEntity.ok(levels);
    }
}
