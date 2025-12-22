package orinnetwork.jpstudy.application.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "사용자 회원가입 요청 DTO")
public class SignUpRequest {

    @NotBlank(message = "이메일은 필수 입력 값입니다.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    @Schema(
            description = "사용자 이메일 (필수, 유효한 이메일 형식)",
            requiredMode = Schema.RequiredMode.REQUIRED,
            format = "email",
            example = "newuser@jpstudy.com"
    )
    private String email;

    @NotBlank(message = "사용자 이름은 필수 입력 값입니다.")
    @Size(min = 2, max = 15, message = "닉네임은 2자 이상 15자 이하로 입력해주세요.")
    @Pattern(regexp = "^[a-zA-Z0-9가-힣_]*$", message = "닉네임은 한글, 영문, 숫자, 밑줄(_)만 사용할 수 있습니다.")
    @Schema(
            description = "사용자 이름/닉네임 (필수)",
            requiredMode = Schema.RequiredMode.REQUIRED,
            minLength = 2,
            maxLength = 15,
            pattern = "^[a-zA-Z0-9가-힣_]*$",
            example = "JP_Master"
    )
    private String username;

    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    @Size(min = 8, message = "비밀번호는 8자 이상이어야 합니다.")
    @Schema(
            description = "비밀번호 (필수)",
            requiredMode = Schema.RequiredMode.REQUIRED,
            minLength = 8,
            format = "password",
            example = "securepass123!"
    )
    private String password;
}