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

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService {

    private final QuestionCategoryRepository categoryRepository;
    private final SectionRepository sectionRepository;

    public CategoryResponse createCategory(CategoryRequest request) {
        Section section = sectionRepository.findById(request.sectionId())
                .orElseThrow(() -> new IllegalArgumentException("Section not found"));

        QuestionCategory newCategory = QuestionCategory.builder()
                .section(section)
                .name(request.name())
                .build();
        QuestionCategory savedCategory = categoryRepository.save(newCategory);
        return CategoryResponse.fromEntity(savedCategory);
    }

    public CategoryResponse updateCategory(Long categoryId, CategoryRequest request) {
        QuestionCategory category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category nod found"));

        Section section = sectionRepository.findById(request.sectionId())
                .orElseThrow(() -> new IllegalArgumentException("Section not found"));

        category.update(section, request.name());
        return CategoryResponse.fromEntity(category);
    }

    public void deleteCategory(Long categoryId) {
        categoryRepository.deleteById(categoryId);
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(CategoryResponse::fromEntity)
                .toList();
    }
}
