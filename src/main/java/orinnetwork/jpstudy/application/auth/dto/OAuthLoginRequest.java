package orinnetwork.jpstudy.application.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "OAuth2 소셜 로그인 요청 DTO")
public class OAuthLoginRequest {

    @NotBlank(message = "인증 코드는 필수입니다.")
    @Schema(
            description = "OAuth2 서버로부터 발급받은 인증 코드 (Authorization Code) (필수)",
            requiredMode = Schema.RequiredMode.REQUIRED,
            example = "4/P7qdt1zB-a-W61f_gA2x-L9N-aP3J1Y"
    )
    private String authorizationCode;

    @NotBlank(message = "제공자 이름은 필수입니다.")
    @Schema(
            description = "로그인 제공자 이름 (예: kakao, naver, google 등) (필수)",
            requiredMode = Schema.RequiredMode.REQUIRED,
            example = "kakao"
    )
    private String provider;
}