package orinnetwork.jpstudy.application.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "인증 토큰 및 사용자 정보 응답 DTO")
public class TokenResponse {

    @Schema(description = "실제 API 요청 시 사용되는 짧은 만료 기간의 접근 토큰 (Access Token)")
    private final String accessToken;

    @Schema(description = "Access Token 만료 시 재발급에 사용되는 긴 만료 기간의 갱신 토큰 (Refresh Token)")
    private final String refreshToken;

    @Schema(description = "로그인한 사용자의 닉네임 또는 이름")
    private final String userName;

    @Schema(description = "Refresh Token의 유효 기간 (밀리초, ms)")
    private long refreshTokenValidityMs;
}