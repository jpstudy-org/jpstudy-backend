package orinnetwork.jpstudy.presentation.auth;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import orinnetwork.jpstudy.application.auth.AuthService;
import orinnetwork.jpstudy.application.auth.OAuthService;
import orinnetwork.jpstudy.application.auth.dto.LoginRequestDto;
import orinnetwork.jpstudy.application.auth.dto.OAuthLoginRequestDto;
import orinnetwork.jpstudy.application.auth.dto.SignUpRequestDto;
import orinnetwork.jpstudy.application.auth.dto.TokenResponseDto;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final OAuthService oAuthService;

    @PostMapping("/signUp")
    public ResponseEntity<TokenResponseDto> signUp(@Valid @RequestBody SignUpRequestDto request) {
        TokenResponseDto token = authService.signUp(request);

        return ResponseEntity.ok(token);
    }

    @PostMapping("/oauth-login")
    public ResponseEntity<TokenResponseDto> oauthLogin(@RequestBody OAuthLoginRequestDto requestDto) {
        return ResponseEntity.ok(oAuthService.login(requestDto));
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponseDto> login(@Valid @RequestBody LoginRequestDto request) {
        TokenResponseDto token = authService.login(request);

        return ResponseEntity.ok(token);
    }
}
