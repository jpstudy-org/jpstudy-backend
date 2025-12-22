package orinnetwork.jpstudy.presentation.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Schema(description = "로그인 및 토큰 재발급 성공 시 반환되는 액세스 토큰 응답 DTO")
public class AccessTokenResponse {

    @Schema(description = "인증에 사용되는 JWT Access Token 값")
    private final String accessToken;

    @Schema(description = "로그인한 사용자의 이름(닉네임)")
    private final String userName;

}
