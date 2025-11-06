package orinnetwork.jpstudy.presentation.auth;

import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import orinnetwork.jpstudy.application.auth.dto.TokenResponseDto;
import orinnetwork.jpstudy.presentation.auth.dto.AccessTokenResponse;

@Component
@RequiredArgsConstructor
public class AuthResponseHelper {

    private final Environment env;

    public ResponseEntity<AccessTokenResponse> createTokenResponse(
            TokenResponseDto tokenDto,
            HttpServletResponse response) {

        boolean isProduction = Arrays.asList(env.getActiveProfiles()).contains("prod");
        String cookieDomain = isProduction ? "test.com" : null;

        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", tokenDto.getRefreshToken())
                .httpOnly(true)
                .secure(isProduction)
                .path("/")
                .maxAge(tokenDto.getRefreshTokenValidityMs() / 1000)
                .domain(cookieDomain)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        return ResponseEntity.ok(
                new AccessTokenResponse(tokenDto.getAccessToken(), tokenDto.getUserName())
        );
    }

    public void clearCookies(HttpServletResponse response) {
        ResponseCookie deleteCookie = ResponseCookie.from("refreshToken", "")
                .maxAge(0)
                .path("/")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, deleteCookie.toString());
    }
}
