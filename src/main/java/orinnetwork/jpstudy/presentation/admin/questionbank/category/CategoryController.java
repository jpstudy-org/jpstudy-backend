package orinnetwork.jpstudy.presentation.admin.questionbank.category;

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
import orinnetwork.jpstudy.application.admin.questionbank.category.CategoryService;
import orinnetwork.jpstudy.application.admin.questionbank.category.dto.CategoryRequest;
import orinnetwork.jpstudy.application.admin.questionbank.category.dto.CategoryResponse;

@Tag(name = "Admin - Question Bank", description = "관리자: 문제 은행 카테고리 관리")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/admin/questionbank/categories")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "카테고리 생성", description = "새로운 문제 카테고리를 생성합니다.")
    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(@RequestBody CategoryRequest request) {
        CategoryResponse createdCategory = categoryService.createCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCategory);
    }

    @Operation(summary = "카테고리 수정", description = "기존 카테고리의 정보를 수정합니다.")
    @PutMapping("/{categoryId}")
    public ResponseEntity<CategoryResponse> updateCategory(
            @Parameter(description = "수정할 카테고리 ID")
            @PathVariable Long categoryId,

            @RequestBody CategoryRequest request) {
        CategoryResponse updateCategory = categoryService.updateCategory(categoryId, request);
        return ResponseEntity.ok(updateCategory);
    }

    @Operation(summary = "카테고리 삭제", description = "특정 카테고리를 삭제합니다.")
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> deleteCategory(
            @Parameter(description = "삭제할 카테고리 ID")
            @PathVariable Long categoryId
    ) {
        categoryService.deleteCategory(categoryId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "모든 카테고리 목록 조회", description = "등록된 모든 카테고리 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        List<CategoryResponse> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }
}