package orinnetwork.jpstudy.presentation.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "Auth API", description = "인증 및 권한 관리 (회원가입, 로그인, 토큰 관리)")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final OAuthService oAuthService;
    private final AuthResponseHelper authResponseHelper;

    @Operation(summary = "일반 회원가입", description = "이메일과 비밀번호를 사용하여 일반 사용자로 신규 가입하고 액세스 토큰을 발급합니다.")
    @PostMapping("/signup")
    public ResponseEntity<AccessTokenResponse> signUp(
            @Valid @RequestBody SignUpRequest request,

            @Parameter(hidden = true)
            HttpServletResponse response,

            @Parameter(hidden = true)
            HttpServletRequest httpServletRequest
    ) {
        final String ipAddress = IpUtil.getClientIp(httpServletRequest);
        final String userAgent = httpServletRequest.getHeader("User-Agent");

        TokenResponse token = authService.signUp(request, ipAddress, userAgent);

        return authResponseHelper.createTokenResponse(token, response);
    }

    @Operation(summary = "OAuth 로그인", description = "소셜 로그인 정보를 이용해 회원가입/로그인을 처리하고 액세스 토큰을 발급합니다.")
    @PostMapping("/oauth-login")
    public ResponseEntity<AccessTokenResponse> oauthLogin(
            @RequestBody OAuthLoginRequest requestDto,

            @Parameter(hidden = true)
            HttpServletResponse response,

            @Parameter(hidden = true)
            HttpServletRequest httpServletRequest
    ) {
        final String ipAddress = IpUtil.getClientIp(httpServletRequest);
        final String userAgent = httpServletRequest.getHeader("User-Agent");

        TokenResponse token = oAuthService.login(requestDto, ipAddress, userAgent);

        return authResponseHelper.createTokenResponse(token, response);
    }

    @Operation(summary = "일반 로그인", description = "이메일과 비밀번호를 사용하여 로그인하고 액세스 토큰을 발급합니다.")
    @PostMapping("/login")
    public ResponseEntity<AccessTokenResponse> login(
            @Valid @RequestBody LoginRequest request,

            @Parameter(hidden = true)
            HttpServletResponse response,

            @Parameter(hidden = true)
            HttpServletRequest httpServletRequest
    ) {
        final String ipAddress = IpUtil.getClientIp(httpServletRequest);
        final String userAgent = httpServletRequest.getHeader("User-Agent");

        TokenResponse token = authService.login(request, ipAddress, userAgent);

        return authResponseHelper.createTokenResponse(token, response);
    }

    @Operation(summary = "로그아웃", description = "서버에서 Refresh Token을 무효화하고 클라이언트의 쿠키를 제거하여 로그아웃 처리합니다.")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @Parameter(hidden = true)
            HttpServletResponse response
    ) {
        authService.logout();
        authResponseHelper.clearCookies(response);

        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "토큰 재발급",
            description = "만료된 Access Token을 갱신합니다. Refresh Token은 HTTP Only Cookie로 전달되어야 합니다."
    )
    @PostMapping("/reissue")
    public ResponseEntity<AccessTokenResponse> reissue(
            @Parameter(description = "Refresh Token (쿠키로 전달)", required = true)
            @CookieValue(name = "refreshToken", required = false) String refreshToken,

            @Parameter(hidden = true)
            HttpServletResponse response
    ) {

        if (refreshToken == null) {
            return ResponseEntity.status(401).body(null);
        }
        TokenResponse tokenResponse = authService.reissueToken(refreshToken);

        return authResponseHelper.createTokenResponse(tokenResponse, response);
    }

    @Operation(summary = "비밀번호 재설정 요청", description = "비밀번호 재설정 링크 또는 코드를 사용자 이메일로 전송하도록 요청합니다.")
    @PostMapping("/password-reset-request")
    public ResponseEntity<Void> requestPasswordReset(
            @Valid @RequestBody PasswordResetRequest request
    ) {
        authService.requestPasswordReset(request);

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "비밀번호 재설정 확인", description = "제공된 인증 코드(토큰)를 확인하고 새 비밀번호로 업데이트합니다.")
    @PostMapping("/password-reset-confirm")
    public ResponseEntity<Void> confirmPasswordReset(
            @Valid @RequestBody PasswordResetConfirm request
    ) {
        authService.confirmPasswordReset(request);
        return ResponseEntity.ok().build();
    }
}