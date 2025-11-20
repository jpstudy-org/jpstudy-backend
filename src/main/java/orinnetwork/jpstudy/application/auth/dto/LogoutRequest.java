package orinnetwork.jpstudy.application.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LogoutRequest {
    @NotBlank(message = "Refresh Token은 필수입니다.")
    private String refreshToken;
}