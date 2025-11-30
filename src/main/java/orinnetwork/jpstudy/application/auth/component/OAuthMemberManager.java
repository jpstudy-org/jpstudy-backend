package orinnetwork.jpstudy.application.auth.component;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import orinnetwork.jpstudy.domain.member.MemberRepository;
import orinnetwork.jpstudy.domain.member.OauthMember;
import orinnetwork.jpstudy.domain.member.Role;
import orinnetwork.jpstudy.infrastructure.exception.CustomException;
import orinnetwork.jpstudy.infrastructure.exception.ErrorCode;

@Component
@RequiredArgsConstructor
public class OAuthMemberManager {

    private final MemberRepository memberRepository;

    @Transactional
    public OauthMember syncMember(String providerName, OAuth2User oAuth2User) {
        String lowerCaseProvider = providerName.toLowerCase();
        Map<String, String> userInfo = extractAttributes(lowerCaseProvider, oAuth2User.getAttributes());

        String providerId = userInfo.get("id");
        String email = userInfo.get("email");
        String username = userInfo.get("name");

        return memberRepository.findByProviderAndProviderId(lowerCaseProvider, providerId)
                .orElseGet(() -> memberRepository.save(new OauthMember(
                        email,
                        username,
                        Role.USER,
                        lowerCaseProvider,
                        providerId
                )));
    }

    private Map<String, String> extractAttributes(String provider, Map<String, Object> attributes) {
        if ("google".equals(provider)) {
            return Map.of(
                    "id", String.valueOf(attributes.get("sub")),
                    "email", String.valueOf(attributes.get("email")),
                    "name", String.valueOf(attributes.get("name"))
            );
        }
        // 추후 카카오, 네이버 추가 시
        throw new CustomException(ErrorCode.OAUTH_PROVIDER_NOT_SUPPORTED, provider);
    }
}
