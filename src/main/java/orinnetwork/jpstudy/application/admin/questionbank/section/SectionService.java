package orinnetwork.jpstudy.application.admin.questionbank.section;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import orinnetwork.jpstudy.application.admin.questionbank.section.dto.SectionResponse;
import orinnetwork.jpstudy.domain.questionbank.SectionRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SectionService {

    private final SectionRepository sectionRepository;

    public List<SectionResponse> getAllSections() {
        return sectionRepository.findAll().stream()
                .map(SectionResponse::fromEntity)
                .toList();
    }
}
