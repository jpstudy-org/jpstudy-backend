package orinnetwork.jpstudy.domain.exam;

import java.io.Serializable;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@EqualsAndHashCode
public class ExamQuestionId implements Serializable {
    private Long exam;
    private Long question;
}
