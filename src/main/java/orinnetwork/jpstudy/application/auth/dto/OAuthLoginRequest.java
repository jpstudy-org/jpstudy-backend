package orinnetwork.jpstudy.application.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OAuthLoginRequest {

    @NotBlank(message = "인증 코드는 필수입니다.")
    private String authorizationCode;

    @NotBlank(message = "제공자 이름은 필수입니다.")
    private String provider;
}
