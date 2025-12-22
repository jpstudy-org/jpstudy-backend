package orinnetwork.jpstudy.application.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "비밀번호 재설정 확인 요청 DTO")
public class PasswordResetConfirm {

    @NotBlank(message = "토큰이 필요합니다.")
    @Schema(
            description = "비밀번호 재설정을 위해 이메일로 발급받은 인증 토큰 (필수)",
            requiredMode = Schema.RequiredMode.REQUIRED,
            example = "a1b2c3d4e5f6g7h8"
    )
    private String token;

    @NotBlank(message = "새 비밀번호를 입력해주세요.")
    @Schema(
            description = "사용자가 새로 설정할 비밀번호 (필수)",
            requiredMode = Schema.RequiredMode.REQUIRED,
            example = "NewPassword!234"
    )
    private String newPassword;
}