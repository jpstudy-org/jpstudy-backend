package orinnetwork.jpstudy.application.member.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberUpdateRequest {

    @NotNull(message = "닉네임은 비워둘 수 없습니다")
    private String username;
}
