package orinnetwork.jpstudy.application.anime.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import org.springframework.data.domain.Page;

@Schema(description = "애니메이션 페이지네이션 응답 DTO")
public record AnimePageResponse(
        @Schema(description = "현재 페이지에 포함된 애니메이션 목록") List<AnimeResponse> content,
        @Schema(description = "전체 페이지 수") int totalPages,
        @Schema(description = "전체 항목 수") long totalElements,
        @Schema(description = "현재 페이지 번호 (0부터 시작)") int currentPage
) {
    public static AnimePageResponse from(Page<AnimeResponse> page) {
        return new AnimePageResponse(
                page.getContent(),
                page.getTotalPages(),
                page.getTotalElements(),
                page.getNumber()
        );
    }
}
