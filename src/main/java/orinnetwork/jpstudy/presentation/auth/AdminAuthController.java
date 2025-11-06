package orinnetwork.jpstudy.presentation.auth;

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
import orinnetwork.jpstudy.application.auth.dto.LoginRequestDto;
import orinnetwork.jpstudy.application.auth.dto.TokenResponseDto;
import orinnetwork.jpstudy.presentation.auth.dto.AccessTokenResponse;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminAuthController {

    private final AdminAuthservice authService;
    private final AuthResponseHelper authResponseHelper;

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
            return ResponseEntity.status(401).build();
        }

        TokenResponseDto tokenResponse = authService.reissueToken(refreshToken);

        return authResponseHelper.createTokenResponse(tokenResponse, response);
    }
}
