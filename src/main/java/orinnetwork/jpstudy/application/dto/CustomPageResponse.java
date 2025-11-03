package orinnetwork.jpstudy.application.dto;

import java.util.List;
import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
public class CustomPageResponse<T> {
    private final List<T> content;
    private final int totalPages;
    private final long totalElements;
    private final int size;
    private final int number;
    private final boolean first;
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
