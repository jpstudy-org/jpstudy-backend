package orinnetwork.jpstudy.application.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "사용자 계정 정보 수정 요청 DTO")
public class MemberUpdateRequest {

    @Schema(description = "새로운 사용자 이름(닉네임). null 또는 생략 시 변경 없음", nullable = true, example = "새로운 닉네임")
    private String username;

    @Pattern(regexp = "^(kr|jp|en)$", message = "지원되지 않는 언어 코드입니다.")
    @Schema(
            description = "선호하는 언어 코드 (필수 입력값 아님). 허용 값: kr, jp, en",
            nullable = true,
            pattern = "^(kr|jp|en)$", // 유효성 검사 패턴 명시
            example = "kr"
    )
    private String languagePreference;

    public MemberUpdateRequest(String username, String languagePreference) {
        this.username = username;
        this.languagePreference = languagePreference;
    }
}
