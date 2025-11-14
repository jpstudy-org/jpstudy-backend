package orinnetwork.jpstudy.application.member.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberUpdateRequest {

    private String username;

    @Pattern(regexp = "^(kr|jp|en)$", message = "지원되지 않는 언어 코드입니다.")
    private String languagePreference;

    public MemberUpdateRequest(String username, String languagePreference) {
        this.username = username;
        this.languagePreference = languagePreference;
    }
}
