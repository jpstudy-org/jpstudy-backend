package orinnetwork.jpstudy.application.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "사용자 로그아웃 요청 DTO (Refresh Token 무효화)")
public class LogoutRequest {

    @NotBlank(message = "Refresh Token은 필수입니다.")
    @Schema(
            description = "무효화(블랙리스트 처리)할 Refresh Token 값 (필수)",
            requiredMode = Schema.RequiredMode.REQUIRED,
            example = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJyZWZyZXNoX3Rva2VuIiwiaWQiOjEyMywiaWF0IjoxNjYyN..."
    )
    private String refreshToken;
}