package orinnetwork.jpstudy.application.auth.component;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.core.user.OAuth2User;
import orinnetwork.jpstudy.domain.member.MemberRepository;
import orinnetwork.jpstudy.domain.member.OauthMember;
import orinnetwork.jpstudy.infrastructure.exception.CustomException;
import orinnetwork.jpstudy.infrastructure.exception.ErrorCode;

@ExtendWith(MockitoExtension.class)
class OAuthMemberManagerTest {

    @InjectMocks
    private OAuthMemberManager oAuthMemberManager;

    @Mock
    private MemberRepository memberRepository;

    @Test
    @DisplayName("신규 구글 회원이면 DB에 저장하고 반환한다")
    void syncMember_new_member() {
        String provider = "google";

        OAuth2User oAuth2User = mock(OAuth2User.class);
        given(oAuth2User.getAttributes()).willReturn(Map.of(
                "sub", "123456789",
                "email", "test@gmail.com",
                "name", "Tester"
        ));

        given(memberRepository.findByProviderAndProviderId("google", "123456789"))
                .willReturn(Optional.empty());

        OauthMember savedMember = OauthMember.from("test@gmail.com", "Tester", "google", "123456789");
        given(memberRepository.save(any(OauthMember.class))).willReturn(savedMember);

        OauthMember result = oAuthMemberManager.syncMember(provider, oAuth2User);

        assertThat(result.getEmail()).isEqualTo("test@gmail.com");

        then(memberRepository).should(times(1)).save(any(OauthMember.class));
    }

    @Test
    @DisplayName("기존 구글 회원이면 저장하지 않고 기존 회원을 반환한다")
    void syncMember_existing_member() {

        String provider = "google";
        OAuth2User oAuth2User = mock(OAuth2User.class);
        given(oAuth2User.getAttributes()).willReturn(Map.of(
                "sub", "123456789",
                "email", "test@gmail.com",
                "name", "Tester"
        ));

        OauthMember existingMember = OauthMember.from("test@gmail.com", "Tester", "google", "123456789");
        given(memberRepository.findByProviderAndProviderId("google", "123456789"))
                .willReturn(Optional.of(existingMember));

        OauthMember result = oAuthMemberManager.syncMember(provider, oAuth2User);

        assertThat(result).isEqualTo(existingMember);
        then(memberRepository).should(never()).save(any(OauthMember.class));
    }

    @Test
    @DisplayName("지원하지 않는 공급자(jpstudy)는 예외")
    void syncMember_fail_unsupported_provider() {
        // given
        String provider = "jpstudy";
        OAuth2User oAuth2User = mock(OAuth2User.class);
        given(oAuth2User.getAttributes()).willReturn(Map.of("id", "something"));

        CustomException ex = assertThrows(CustomException.class,
                () -> oAuthMemberManager.syncMember(provider, oAuth2User));

        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.OAUTH_PROVIDER_NOT_SUPPORTED);

        then(memberRepository).shouldHaveNoInteractions();
    }
}