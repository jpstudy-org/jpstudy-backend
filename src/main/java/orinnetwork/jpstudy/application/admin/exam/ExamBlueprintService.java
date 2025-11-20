package orinnetwork.jpstudy.application.admin.exam;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import orinnetwork.jpstudy.application.admin.exam.dto.BlueprintDetailRequest;
import orinnetwork.jpstudy.application.admin.exam.dto.BlueprintResponse;
import orinnetwork.jpstudy.application.admin.exam.dto.CreateBlueprintRequest;
import orinnetwork.jpstudy.application.dto.CustomPageResponse;
import orinnetwork.jpstudy.domain.exam.BlueprintDetail;
import orinnetwork.jpstudy.domain.exam.ExamBlueprint;
import orinnetwork.jpstudy.domain.exam.ExamBlueprintRepository;
import orinnetwork.jpstudy.domain.questionbank.Level;
import orinnetwork.jpstudy.domain.questionbank.LevelRepository;
import orinnetwork.jpstudy.domain.questionbank.QuestionCategory;
import orinnetwork.jpstudy.domain.questionbank.QuestionCategoryRepository;
import orinnetwork.jpstudy.infrastructure.exception.CustomException;
import orinnetwork.jpstudy.infrastructure.exception.ErrorCode;

@Service
@RequiredArgsConstructor
@Transactional
public class ExamBlueprintService {

    private final ExamBlueprintRepository blueprintRepository;
    private final LevelRepository levelRepository;
    private final QuestionCategoryRepository categoryRepository;

    public Long createBlueprint(CreateBlueprintRequest request) {
        Level level = levelRepository.findById(request.levelId())
                .orElseThrow(() -> new CustomException(ErrorCode.LEVEL_NOT_FOUND));

        ExamBlueprint blueprint = ExamBlueprint.builder()
                .title(request.title())
                .description(request.description())
                .level(level)
                .totalTimeMinutes(request.totalTimeMinutes())
                .build();

        for (BlueprintDetailRequest detailReq : request.details()) {
            QuestionCategory category = categoryRepository.findById(detailReq.categoryId())
                    .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));

            BlueprintDetail detail = BlueprintDetail.builder()
                    .category(category)
                    .questionCount(detailReq.count())
                    .sequence(detailReq.sequence())
                    .build();

            blueprint.addDetail(detail);
        }

        ExamBlueprint savedBlueprint = blueprintRepository.save(blueprint);

        return savedBlueprint.getId();
    }

    // 단건 조회
    public BlueprintResponse getBlueprint(Long id) {
        ExamBlueprint blueprint = blueprintRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.EXAM_NOT_FOUND)); // 혹은 BLUEPRINT_NOT_FOUND

        return BlueprintResponse.from(blueprint);
    }

    // 목록 조회 (페이징)
    public CustomPageResponse<BlueprintResponse> getAllBlueprints(Pageable pageable) {
        Page<ExamBlueprint> page = blueprintRepository.findAll(pageable);
        Page<BlueprintResponse> responses = page.map(BlueprintResponse::from);
        return new CustomPageResponse<>(responses);
    }

    // 수정
    @Transactional
    public void updateBlueprint(Long id, CreateBlueprintRequest request) {
        ExamBlueprint blueprint = blueprintRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.EXAM_NOT_FOUND));

        Level level = levelRepository.findById(request.levelId())
                .orElseThrow(() -> new CustomException(ErrorCode.LEVEL_NOT_FOUND));

        blueprint.updateInfo(
                request.title(),
                request.description(),
                level,
                request.totalTimeMinutes()
        );

        blueprint.clearDetails();

        for (BlueprintDetailRequest detailReq : request.details()) {
            QuestionCategory category = categoryRepository.findById(detailReq.categoryId())
                    .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));

            BlueprintDetail newDetail = BlueprintDetail.builder()
                    .category(category)
                    .questionCount(detailReq.count())
                    .sequence(detailReq.sequence())
                    .build();

            blueprint.addDetail(newDetail);
        }
    }

    // 삭제
    @Transactional
    public void deleteBlueprint(Long id) {
        ExamBlueprint blueprint = blueprintRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.EXAM_NOT_FOUND));

        blueprintRepository.delete(blueprint);
    }
}
