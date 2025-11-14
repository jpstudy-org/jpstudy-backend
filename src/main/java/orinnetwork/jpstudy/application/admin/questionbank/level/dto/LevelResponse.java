package orinnetwork.jpstudy.application.admin.questionbank.level.dto;

import orinnetwork.jpstudy.domain.questionbank.Level;

public record LevelResponse(
        Long id,
        String name
) {
    public static LevelResponse fromEntity(Level level) {
        return new LevelResponse(level.getId(), level.getName());
    }
}