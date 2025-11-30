package orinnetwork.jpstudy.application.exam.dto;

import java.util.List;
import orinnetwork.jpstudy.domain.exam.MemberAnswer;

public record GradeResult(
        int score,
        List<MemberAnswer> answers
) {
}
