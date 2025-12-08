package orinnetwork.jpstudy.presentation.auth;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import orinnetwork.jpstudy.infrastructure.config.AppProperties;

@Hidden
@RestController
@RequiredArgsConstructor
public class OAuthCallbackController {

    private final AppProperties appProperties;

    @GetMapping("/login/oauth2/code/google")
    public void googleCallback(@RequestParam String code, HttpServletResponse response) throws IOException {
        String redirectUrl = appProperties.getFrontend().getBaseUri() + "/auth/callback/?code=" + code;
        response.sendRedirect(redirectUrl);
    }
}
