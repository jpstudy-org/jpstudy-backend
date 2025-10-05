package orinnetwork.jpstudy.application.auth;

import jakarta.transaction.Transactional;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.endpoint.RestClientAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationExchange;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationResponse;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import orinnetwork.jpstudy.application.auth.dto.OAuthLoginRequestDto;
import orinnetwork.jpstudy.application.auth.dto.TokenResponseDto;
import orinnetwork.jpstudy.domain.member.MemberRepository;
import orinnetwork.jpstudy.domain.member.OauthMember;
import orinnetwork.jpstudy.domain.member.Role;
import orinnetwork.jpstudy.infrastructure.jwt.JwtProvider;

@Service
@RequiredArgsConstructor
public class OAuthService {

    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;
    private final OAuth2AuthorizedClientManager oAuth2AuthorizedClientManager;
    private final InMemoryClientRegistrationRepository clientRegistrationRepository;

    @Transactional
    public TokenResponseDto login(OAuthLoginRequestDto requestDto) {
        ClientRegistration provider = clientRegistrationRepository.findByRegistrationId(requestDto.getProvider());

        if (provider == null) {
            throw new IllegalArgumentException("지원하지 않는 소셜 로그인입니다: " + requestDto.getProvider());
        }

        OAuth2AccessTokenResponse tokenResponse = getToken(provider, requestDto.getAuthorizationCode());

        OAuth2User oAuth2User = getUserInfo(provider, tokenResponse);

        OauthMember member = saveOrUpdate(oAuth2User, requestDto.getProvider());

        String accessToken = jwtProvider.createAccessToken(member.getId(), member.getRole());
        String refreshToken = jwtProvider.createRefreshToken(member.getId());

        return new TokenResponseDto(accessToken, refreshToken);
    }


    private OAuth2AccessTokenResponse getToken(ClientRegistration provider, String authorizationCode) {
        OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> tokenResponseClient =
                new RestClientAuthorizationCodeTokenResponseClient();

        OAuth2AuthorizationExchange authorizationExchange = new OAuth2AuthorizationExchange(
                OAuth2AuthorizationRequest.authorizationCode()
                        .clientId(provider.getClientId())
                        .authorizationUri(provider.getProviderDetails().getAuthorizationUri())
                        .redirectUri(provider.getRedirectUri())
                        .scopes(provider.getScopes())
                        .build(),
                OAuth2AuthorizationResponse.success(authorizationCode)
                        .redirectUri(provider.getRedirectUri())
                        .build()
        );

        OAuth2AuthorizationCodeGrantRequest grantRequest = new OAuth2AuthorizationCodeGrantRequest(provider, authorizationExchange);
        return tokenResponseClient.getTokenResponse(grantRequest);
    }

    private OAuth2User getUserInfo(ClientRegistration provider, OAuth2AccessTokenResponse tokenResponse) {
        DefaultOAuth2UserService userService = new DefaultOAuth2UserService();
        OAuth2UserRequest userRequest = new OAuth2UserRequest(provider, tokenResponse.getAccessToken());

        try {
            return userService.loadUser(userRequest);
        } catch (OAuth2AuthenticationException e) {
            throw new RuntimeException("소셜 로그인 사용자 정보를 가져오는 데 실패했습니다.", e);
        }
    }

    private OauthMember saveOrUpdate(OAuth2User oAuth2User, String providerName) {
        Map<String, Object> attributes = oAuth2User.getAttributes();
        String lowerCaseProviderName = providerName.toLowerCase();

        String providerId;
        String email;
        String username;

        switch (lowerCaseProviderName) {
            case "google" -> {
                providerId = attributes.get("sub").toString();
                email = attributes.get("email").toString();
                username = attributes.get("name").toString();
            }
            default -> throw new IllegalArgumentException("지원하지 않는 소셜 로그인입니다: " + providerName);
        }

        Optional<OauthMember> memberOptional = memberRepository.findByProviderAndProviderId(lowerCaseProviderName, providerId);

        OauthMember member;
        if (memberOptional.isPresent()) {
            member = memberOptional.get();
        }
        else {
            member = new OauthMember(
                    email,
                    username,
                    Role.USER,
                    lowerCaseProviderName,
                    providerId
            );
            memberRepository.save(member);
        }
        return member;
    }
}