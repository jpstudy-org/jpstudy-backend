package orinnetwork.jpstudy.application.auth;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import orinnetwork.jpstudy.application.auth.dto.OAuthLoginRequestDto;
import orinnetwork.jpstudy.domain.member.MemberRepository;
import orinnetwork.jpstudy.infrastructure.jwt.JwtProvider;

@RequiredArgsConstructor
public class OAuthService {

    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;
    private final OAuth2AuthorizedClientManager oAuth2AuthorizedClientManager;
}
