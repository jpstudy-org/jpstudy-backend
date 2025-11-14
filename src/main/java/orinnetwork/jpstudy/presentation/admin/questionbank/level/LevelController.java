package orinnetwork.jpstudy.presentation.admin.questionbank.level;

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
import orinnetwork.jpstudy.application.admin.questionbank.level.LevelService;
import orinnetwork.jpstudy.application.admin.questionbank.level.dto.LevelRequest;
import orinnetwork.jpstudy.application.admin.questionbank.level.dto.LevelResponse;

@RestController
@RequestMapping("/api/admin/questionbank/levels")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class LevelController {

    private final LevelService levelService;

    @PostMapping
    public ResponseEntity<LevelResponse> createLevel(@RequestBody LevelRequest request) {
        LevelResponse createdLevel = levelService.createLevel(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdLevel);
    }

    @PutMapping("/{levelId}")
    public ResponseEntity<LevelResponse> updateLevel(
            @PathVariable Long levelId,
            @RequestBody LevelRequest request) {
        LevelResponse updatedLevel = levelService.updateLevel(levelId, request);
        return ResponseEntity.ok(updatedLevel);
    }

    @DeleteMapping("/{levelId}")
    public ResponseEntity<Void> deleteLevel(@PathVariable Long levelId) {
        levelService.deleteLevel(levelId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{levelId}")
    public ResponseEntity<LevelResponse> getLevel(@PathVariable Long levelId) {
        LevelResponse level = levelService.getLevel(levelId);
        return ResponseEntity.ok(level);
    }

    @GetMapping
    public ResponseEntity<List<LevelResponse>> getAllLevels() {
        List<LevelResponse> levels = levelService.getAllLevels();
        return ResponseEntity.ok(levels);
    }
}
