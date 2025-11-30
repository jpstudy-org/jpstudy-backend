package orinnetwork.jpstudy.application.auth.component;

import lombok.RequiredArgsConstructor;
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
import org.springframework.stereotype.Component;
import orinnetwork.jpstudy.infrastructure.exception.CustomException;
import orinnetwork.jpstudy.infrastructure.exception.ErrorCode;

@Component
@RequiredArgsConstructor
public class OAuth2Client {

    private final InMemoryClientRegistrationRepository clientRegistrationRepository;

    public OAuth2User fetchUser(String providerName, String authorizationCode) {
        ClientRegistration provider = clientRegistrationRepository.findByRegistrationId(providerName);
        if (provider == null) {
            throw new CustomException(ErrorCode.OAUTH_PROVIDER_NOT_SUPPORTED, providerName);
        }

        OAuth2AccessTokenResponse tokenResponse = getToken(provider, authorizationCode);
        return getUserInfo(provider, tokenResponse);
    }

    private OAuth2AccessTokenResponse getToken(ClientRegistration provider, String code) {
        OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> tokenClient =
                new RestClientAuthorizationCodeTokenResponseClient();

        OAuth2AuthorizationExchange exchange = new OAuth2AuthorizationExchange(
                OAuth2AuthorizationRequest.authorizationCode()
                        .clientId(provider.getClientId())
                        .authorizationUri(provider.getProviderDetails().getAuthorizationUri())
                        .redirectUri(provider.getRedirectUri())
                        .scopes(provider.getScopes())
                        .build(),
                OAuth2AuthorizationResponse.success(code)
                        .redirectUri(provider.getRedirectUri())
                        .build()
        );

        return tokenClient.getTokenResponse(new OAuth2AuthorizationCodeGrantRequest(provider, exchange));
    }

    private OAuth2User getUserInfo(ClientRegistration provider, OAuth2AccessTokenResponse tokenResponse) {
        DefaultOAuth2UserService userService = new DefaultOAuth2UserService();
        try {
            return userService.loadUser(new OAuth2UserRequest(provider, tokenResponse.getAccessToken()));
        } catch (OAuth2AuthenticationException e) {
            throw new CustomException(ErrorCode.OAUTH_FAIL);
        }
    }
}