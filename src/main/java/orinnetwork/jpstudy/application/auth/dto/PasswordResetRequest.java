package orinnetwork.jpstudy.application.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "비밀번호 재설정 링크/토큰 발급 요청 DTO")
public class PasswordResetRequest {

    @NotBlank(message = "이메일을 입력해주세요.")
    @Email
    @Schema(
            description = "비밀번호 재설정을 요청할 사용자 이메일 (필수, 이메일 형식이어야 함)",
            requiredMode = Schema.RequiredMode.REQUIRED,
            format = "email", // 이메일 형식임을 명시
            example = "user@example.com"
    )
    private String email;
}