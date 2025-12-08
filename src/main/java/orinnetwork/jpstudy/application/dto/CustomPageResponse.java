package orinnetwork.jpstudy.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
@Schema(description = "커스텀 페이지네이션 응답 DTO. 목록 조회 결과의 기본 포맷입니다.")
public class CustomPageResponse<T> {

    @Schema(description = "현재 페이지에 포함된 실제 데이터 목록")
    private final List<T> content;

    @Schema(description = "전체 페이지 수")
    private final int totalPages;

    @Schema(description = "전체 항목(데이터) 개수")
    private final long totalElements;

    @Schema(description = "페이지 당 항목 개수 (요청 size)")
    private final int size;

    @Schema(description = "현재 페이지 번호 (0부터 시작)")
    private final int number;

    @Schema(description = "현재 페이지가 첫 페이지인지 여부")
    private final boolean first;

    @Schema(description = "현재 페이지가 마지막 페이지인지 여부")
    private final boolean last;

    public CustomPageResponse(Page<T> page) {
        this.content = page.getContent();
        this.totalPages = page.getTotalPages();
        this.totalElements = page.getTotalElements();
        this.size = page.getSize();
        this.number = page.getNumber();
        this.first = page.isFirst();
        this.last = page.isLast();
    }
}
