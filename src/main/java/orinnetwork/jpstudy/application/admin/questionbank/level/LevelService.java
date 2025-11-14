package orinnetwork.jpstudy.application.admin.questionbank.level;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import orinnetwork.jpstudy.application.admin.questionbank.level.dto.LevelRequest;
import orinnetwork.jpstudy.application.admin.questionbank.level.dto.LevelResponse;
import orinnetwork.jpstudy.domain.questionbank.Level;
import orinnetwork.jpstudy.domain.questionbank.LevelRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class LevelService {

    private final LevelRepository levelRepository;

    public LevelResponse createLevel(LevelRequest request) {
        Level newLevel = Level.builder()
                .name(request.name())
                .build();
        Level savedLevel = levelRepository.save(newLevel);
        return LevelResponse.fromEntity(savedLevel);
    }

    public LevelResponse updateLevel(Long levelId, LevelRequest request) {
        Level level = levelRepository.findById(levelId)
                .orElseThrow(() -> new IllegalArgumentException("Level not found"));

        level.updateName(request.name());
        return LevelResponse.fromEntity(level);
    }

    public void deleteLevel(Long levelId) {
        levelRepository.deleteById(levelId);
    }

    @Transactional(readOnly = true)
    public LevelResponse getLevel(Long levelId) {
        Level level = levelRepository.findById(levelId)
                .orElseThrow(() -> new IllegalArgumentException("Level not found"));
        return LevelResponse.fromEntity(level);
    }

    @Transactional(readOnly = true)
    public List<LevelResponse> getAllLevels() {
        return levelRepository.findAll().stream()
                .map(LevelResponse::fromEntity)
                .toList();
    }
}