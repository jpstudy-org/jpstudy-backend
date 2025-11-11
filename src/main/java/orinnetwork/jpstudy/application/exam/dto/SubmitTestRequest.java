package orinnetwork.jpstudy.application.exam.dto;

import java.util.List;

public record SubmitTestRequest(
        List<UserAnswer> answers
) { }