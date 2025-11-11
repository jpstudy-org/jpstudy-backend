package orinnetwork.jpstudy.application.admin.questionbank.section.dto;

import orinnetwork.jpstudy.domain.questionbank.Section;

public record SectionResponse (
        Long id,
        String name
){
    public static SectionResponse fromEntity(Section section) {
        return new SectionResponse(section.getId(), section.getName());
    }
}