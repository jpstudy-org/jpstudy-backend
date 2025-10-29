package orinnetwork.jpstudy.presentation.auth;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import orinnetwork.jpstudy.application.auth.AuthService;
import orinnetwork.jpstudy.application.auth.OAuthService;
import orinnetwork.jpstudy.application.auth.dto.LoginRequestDto;
import orinnetwork.jpstudy.application.auth.dto.OAuthLoginRequestDto;
import orinnetwork.jpstudy.application.auth.dto.SignUpRequestDto;
import orinnetwork.jpstudy.application.auth.dto.TokenResponseDto;
import orinnetwork.jpstudy.infrastructure.jwt.JwtProvider;
import orinnetwork.jpstudy.presentation.auth.dto.AccessTokenResponse;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final OAuthService oAuthService;
    private final AuthResponseHelper authResponseHelper;

    @PostMapping("/signup")
    public ResponseEntity<AccessTokenResponse> signUp(
            @Valid @RequestBody SignUpRequestDto request,
            HttpServletResponse response
    ) {
        TokenResponseDto token = authService.signUp(request);

        return authResponseHelper.createTokenResponse(token, response);
    }

    @PostMapping("/oauth-login")
    public ResponseEntity<AccessTokenResponse> oauthLogin(
            @RequestBody OAuthLoginRequestDto requestDto,
            HttpServletResponse response
    ) {
        TokenResponseDto token = oAuthService.login(requestDto);

        return authResponseHelper.createTokenResponse(token, response);
    }

    @PostMapping("/login")
    public ResponseEntity<AccessTokenResponse> login(
            @Valid @RequestBody LoginRequestDto request,
            HttpServletResponse response
    ) {
        TokenResponseDto token = authService.login(request);

        return authResponseHelper.createTokenResponse(token, response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        authService.logout();
        authResponseHelper.clearCookies(response);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/reissue")
    public ResponseEntity<AccessTokenResponse> reissue(
            @CookieValue(name = "refreshToken", required = false) String refreshToken,
            HttpServletResponse response
    ) {

        if (refreshToken == null) {
            return ResponseEntity.status(401).body(null);
        }
        TokenResponseDto tokenResponse = authService.reissueToken(refreshToken);

        return authResponseHelper.createTokenResponse(tokenResponse, response);
    }
}