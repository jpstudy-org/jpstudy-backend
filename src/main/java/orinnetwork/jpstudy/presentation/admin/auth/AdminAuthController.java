package orinnetwork.jpstudy.presentation.admin.auth;

import jakarta.validation.Valid;
import java.io.Console;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import orinnetwork.jpstudy.application.admin.auth.AdminAuthservice;
import orinnetwork.jpstudy.application.admin.auth.dto.AdminLoginRequest;
import orinnetwork.jpstudy.application.admin.auth.dto.AdminTokenResponse;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminAuthController {

    private final AdminAuthservice authService;

    @PostMapping("/login")
    public ResponseEntity<AdminTokenResponse> login(@Valid @RequestBody AdminLoginRequest request) {
        AdminTokenResponse token = authService.login(request);

        return ResponseEntity.ok(token);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        authService.logout();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reissue")
    public ResponseEntity<AdminTokenResponse> reissue(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {

        String refreshToken = extractToken(authHeader);
        if (refreshToken == null) {
            return ResponseEntity.badRequest().body(null);
        }

        AdminTokenResponse tokenResponse = authService.reissueToken(refreshToken);
        return ResponseEntity.ok(tokenResponse);
    }

    private String extractToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}
