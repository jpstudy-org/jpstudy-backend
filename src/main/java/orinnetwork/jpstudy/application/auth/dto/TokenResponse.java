package orinnetwork.jpstudy.application.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenResponse {

    private final String accessToken;
    private final String refreshToken;
    private final String userName;
    private long refreshTokenValidityMs;
}
