package orinnetwork.jpstudy.application.admin.questionbank.section.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import orinnetwork.jpstudy.domain.questionbank.Section;

@Schema(description = "문제 은행 섹션(Section) 정보 응답 DTO")
public record SectionResponse(
        @Schema(description = "섹션의 고유 ID")
        Long id,

        @Schema(description = "섹션의 이름 (예: 문법, 어휘, 독해)")
        String name
) {
    public static SectionResponse fromEntity(Section section) {
        return new SectionResponse(section.getId(), section.getName());
    }
}