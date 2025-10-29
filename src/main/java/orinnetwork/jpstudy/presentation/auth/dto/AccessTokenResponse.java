package orinnetwork.jpstudy.presentation.auth.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AccessTokenResponse {

    private final String accessToken;
    private final String userName;

}
