package orinnetwork.jpstudy.presentation.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import orinnetwork.jpstudy.application.auth.AdminAuthservice;
import orinnetwork.jpstudy.application.auth.dto.LoginRequest;
import orinnetwork.jpstudy.application.auth.dto.TokenResponse;
import orinnetwork.jpstudy.infrastructure.util.IpUtil;
import orinnetwork.jpstudy.presentation.auth.dto.AccessTokenResponse;

@Tag(name = "Admin Auth API", description = "관리자 인증 및 토큰 관리")
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminAuthController {

    private final AdminAuthservice authService;
    private final AuthResponseHelper authResponseHelper;

    @Operation(summary = "관리자 로그인", description = "관리자 계정으로 로그인하고 Access Token을 발급합니다.")
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

    @Operation(summary = "관리자 로그아웃", description = "관리자 세션을 무효화하고 쿠키를 제거합니다.")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @Parameter(hidden = true)
            HttpServletResponse response
    ) {
        authService.logout();
        authResponseHelper.clearCookies(response);

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "관리자 토큰 재발급", description = "만료된 Access Token을 Refresh Token을 통해 갱신합니다.")
    @PostMapping("/reissue")
    public ResponseEntity<AccessTokenResponse> reissue(
            @Parameter(description = "Refresh Token (쿠키로 전달)", required = true)
            @CookieValue(name = "refreshToken", required = false) String refreshToken,

            @Parameter(hidden = true)
            HttpServletResponse response
    ) {

        if (refreshToken == null) {
            return ResponseEntity.status(401).build();
        }

        TokenResponse tokenResponse = authService.reissueToken(refreshToken);

        return authResponseHelper.createTokenResponse(tokenResponse, response);
    }
}
