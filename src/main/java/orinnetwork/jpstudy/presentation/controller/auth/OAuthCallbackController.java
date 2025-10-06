package orinnetwork.jpstudy.presentation.controller.auth;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import orinnetwork.jpstudy.presentation.config.AppProperties;

@RestController
@RequiredArgsConstructor
public class OAuthCallbackController {

    private final AppProperties appProperties;

    @GetMapping("/login/oauth2/code/google")
    public void googleCallback(@RequestParam String code, HttpServletResponse response) throws IOException {
        String redirectUrl = appProperties.getFrontend().getBaseUri() + "auth/callback/?code=" + code;
        response.sendRedirect(redirectUrl);
    }
}
