package orinnetwork.jpstudy.presentation.exam;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import orinnetwork.jpstudy.application.admin.exam.ExamBlueprintService;
import orinnetwork.jpstudy.application.admin.exam.dto.BlueprintResponse;
import orinnetwork.jpstudy.application.dto.CustomPageResponse;
import orinnetwork.jpstudy.application.exam.ExamService;
import orinnetwork.jpstudy.application.exam.dto.ExamTakingResponse;
import orinnetwork.jpstudy.application.exam.dto.StartTestResponse;
import orinnetwork.jpstudy.infrastructure.security.CustomUserDetails;

@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Exam API", description = "모의고사(Exam) 진행, 시작 및 출제 양식(Blueprint) 조회")
@RestController
@RequestMapping("/api/exams")
@RequiredArgsConstructor
public class ExamController {

    private final ExamService examService;
    private final ExamBlueprintService blueprintService;

    @Operation(
            summary = "진행 중인 시험 조회",
            description = "현재 사용자가 이어서 풀 수 있는 진행 중인 시험이 있는지 확인합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "진행 중인 시험 데이터 반환"),
                    @ApiResponse(responseCode = "204", description = "진행 중인 시험이 없음 (No Content)")
            }
    )
    @GetMapping("/ongoing")
    public ResponseEntity<StartTestResponse> getOngoingExam(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        StartTestResponse response = examService.getOngoingExam(userDetails.getMemberId());

        if (response == null) {
            return ResponseEntity.noContent().build(); // 204: 없음
        }

        return ResponseEntity.ok(response); // 200: 있음 (데이터 포함)
    }

    @Operation(summary = "이용 가능한 시험 출제 양식(Blueprint) 목록 조회", description = "사용자가 시작할 수 있는 시험 양식 목록을 페이지네이션하여 조회합니다.")
    @GetMapping("/blueprints")
    public ResponseEntity<CustomPageResponse<BlueprintResponse>> getAvailableBlueprints(
            @ParameterObject
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(blueprintService.getAllBlueprints(pageable));
    }

    @Operation(summary = "새 시험 시작", description = "특정 출제 양식(Blueprint)을 기반으로 새로운 시험을 시작합니다.")
    @PostMapping("/start/{blueprintId}")
    public ResponseEntity<StartTestResponse> startExam(
            @Parameter(description = "시험 시작에 사용할 출제 양식 ID")
            @PathVariable Long blueprintId,

            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        String examTitle = "모의고사 (" + LocalDate.now() + ")";

        StartTestResponse response = examService.startExamByBlueprint(
                blueprintId,
                userDetails.getMemberId(),
                examTitle
        );

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "특정 시험 상세 정보 조회", description = "진행 중이거나 완료된 특정 시험의 상세 정보를 조회합니다.")
    @GetMapping("/{examId}")
    public ResponseEntity<ExamTakingResponse> getExamDetails(
            @Parameter(description = "조회할 시험 ID")
            @PathVariable Long examId
    ) {
        ExamTakingResponse response = examService.getExamDetails(examId);
        return ResponseEntity.ok(response);
    }
}