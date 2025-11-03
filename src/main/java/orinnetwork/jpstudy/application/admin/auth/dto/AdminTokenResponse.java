package orinnetwork.jpstudy.application.admin.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AdminTokenResponse {

    private final String accessToken;
    private final String refreshToken;
}
