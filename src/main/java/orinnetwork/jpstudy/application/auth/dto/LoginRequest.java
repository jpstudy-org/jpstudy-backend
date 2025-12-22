package orinnetwork.jpstudy.application.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "사용자 로그인 요청 DTO")
public class LoginRequest {

    @NotBlank(message = "이메일 입력은 필수 값입니다.")
    @Schema(
            description = "사용자 이메일 (필수)",
            requiredMode = Schema.RequiredMode.REQUIRED,
            example = "user@example.com"
    )
    private String email;

    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    @Schema(
            description = "비밀번호 (필수)",
            requiredMode = Schema.RequiredMode.REQUIRED,
            example = "password123!"
    )
    private String password;
}