package orinnetwork.jpstudy.application.admin.questionbank.level.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import orinnetwork.jpstudy.domain.questionbank.Level;

@Schema(description = "문제 은행 난이도 레벨(Level) 정보 응답 DTO")
public record LevelResponse(
        @Schema(description = "난이도 레벨의 고유 ID")
        Long id,

        @Schema(description = "난이도 레벨의 이름 (예: N5, N4, N3 등)")
        String name
) {
    public static LevelResponse fromEntity(Level level) {
        return new LevelResponse(level.getId(), level.getName());
    }
}