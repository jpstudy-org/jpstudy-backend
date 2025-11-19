package orinnetwork.jpstudy.application.admin.questionbank.category;

import com.nimbusds.openid.connect.sdk.id.SectorID;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import orinnetwork.jpstudy.application.admin.questionbank.category.dto.CategoryRequest;
import orinnetwork.jpstudy.application.admin.questionbank.category.dto.CategoryResponse;
import orinnetwork.jpstudy.domain.questionbank.QuestionCategory;
import orinnetwork.jpstudy.domain.questionbank.QuestionCategoryRepository;
import orinnetwork.jpstudy.domain.questionbank.Section;
import orinnetwork.jpstudy.domain.questionbank.SectionRepository;
import orinnetwork.jpstudy.infrastructure.exception.CustomException;
import orinnetwork.jpstudy.infrastructure.exception.ErrorCode;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService {

    private final QuestionCategoryRepository categoryRepository;
    private final SectionRepository sectionRepository;

    public CategoryResponse createCategory(CategoryRequest request) {
        Section section = sectionRepository.findById(request.sectionId())
                .orElseThrow(() -> new CustomException(ErrorCode.SECTION_NOT_FOUND));

        QuestionCategory newCategory = QuestionCategory.builder()
                .section(section)
                .name(request.name())
                .build();
        QuestionCategory savedCategory = categoryRepository.save(newCategory);
        return CategoryResponse.fromEntity(savedCategory);
    }

    public CategoryResponse updateCategory(Long categoryId, CategoryRequest request) {
        QuestionCategory category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));

        Section section = sectionRepository.findById(request.sectionId())
                .orElseThrow(() -> new CustomException(ErrorCode.SECTION_NOT_FOUND));

        category.update(section, request.name());
        return CategoryResponse.fromEntity(category);
    }

    public void deleteCategory(Long categoryId) {
        QuestionCategory category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));

        categoryRepository.delete(category);
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(CategoryResponse::fromEntity)
                .toList();
    }
}
