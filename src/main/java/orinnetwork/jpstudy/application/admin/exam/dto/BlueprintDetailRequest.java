package orinnetwork.jpstudy.application.admin.exam.dto;

public record BlueprintDetailRequest(
        Long categoryId,
        int count,
        int sequence
) { }
