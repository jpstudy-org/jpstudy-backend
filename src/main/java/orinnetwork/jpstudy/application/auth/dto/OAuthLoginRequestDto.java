package orinnetwork.jpstudy.application.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OAuthLoginRequestDto {

    @NotBlank
    private String authorizationCode;
}
