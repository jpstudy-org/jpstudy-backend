package orinnetwork.jpstudy.presentation.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import orinnetwork.jpstudy.application.auth.AuthService;
import orinnetwork.jpstudy.application.auth.OAuthService;
import orinnetwork.jpstudy.application.auth.dto.LoginRequest;
import orinnetwork.jpstudy.application.auth.dto.OAuthLoginRequest;
import orinnetwork.jpstudy.application.auth.dto.PasswordResetConfirm;
import orinnetwork.jpstudy.application.auth.dto.PasswordResetRequest;
import orinnetwork.jpstudy.application.auth.dto.SignUpRequest;
import orinnetwork.jpstudy.application.auth.dto.TokenResponse;
import orinnetwork.jpstudy.infrastructure.util.IpUtil;
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
            @Valid @RequestBody SignUpRequest request,
            HttpServletResponse response,
            HttpServletRequest httpServletRequest
    ) {
        final String ipAddress = IpUtil.getClientIp(httpServletRequest);
        final String userAgent = httpServletRequest.getHeader("User-Agent");

        TokenResponse token = authService.signUp(request, ipAddress, userAgent);

        return authResponseHelper.createTokenResponse(token, response);
    }

    @PostMapping("/oauth-login")
    public ResponseEntity<AccessTokenResponse> oauthLogin(
            @RequestBody OAuthLoginRequest requestDto,
            HttpServletResponse response,
            HttpServletRequest httpServletRequest
    ) {
        final String ipAddress = IpUtil.getClientIp(httpServletRequest);
        final String userAgent = httpServletRequest.getHeader("User-Agent");

        TokenResponse token = oAuthService.login(requestDto, ipAddress, userAgent);

        return authResponseHelper.createTokenResponse(token, response);
    }

    @PostMapping("/login")
    public ResponseEntity<AccessTokenResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response,
            HttpServletRequest httpServletRequest
    ) {
        final String ipAddress = IpUtil.getClientIp(httpServletRequest);
        final String userAgent = httpServletRequest.getHeader("User-Agent");

        TokenResponse token = authService.login(request, ipAddress, userAgent);

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
        TokenResponse tokenResponse = authService.reissueToken(refreshToken);

        return authResponseHelper.createTokenResponse(tokenResponse, response);
    }

    @PostMapping("/password-reset-request")
    public ResponseEntity<Void> requestPasswordReset(
            @Valid @RequestBody PasswordResetRequest request
    ) {
        authService.requestPasswordReset(request);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/password-reset-confirm")
    public ResponseEntity<Void> confirmPasswordReset(
            @Valid @RequestBody PasswordResetConfirm request
    ) {
        authService.confirmPasswordReset(request);
        return ResponseEntity.ok().build();
    }
}