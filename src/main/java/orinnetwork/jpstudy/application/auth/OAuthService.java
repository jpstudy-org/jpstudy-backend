package orinnetwork.jpstudy.application.auth;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import orinnetwork.jpstudy.application.auth.component.LoginHistoryRecorder;
import orinnetwork.jpstudy.application.auth.component.LoginLockManager;
import orinnetwork.jpstudy.application.auth.component.OAuth2Client;
import orinnetwork.jpstudy.application.auth.component.OAuthMemberManager;
import orinnetwork.jpstudy.application.auth.component.TokenManager;
import orinnetwork.jpstudy.application.auth.dto.OAuthLoginRequest;
import orinnetwork.jpstudy.application.auth.dto.TokenResponse;
import orinnetwork.jpstudy.domain.member.OauthMember;

@Service
@RequiredArgsConstructor
public class OAuthService {

    private final OAuth2Client oAuth2Client;
    private final OAuthMemberManager oAuthMemberManager;
    private final TokenManager tokenManager;
    private final LoginHistoryRecorder loginHistoryRecorder;
    private final LoginLockManager loginLockManager;

    @Transactional
    public TokenResponse login(OAuthLoginRequest request, String ipAddress, String userAgent) {
        OAuth2User oAuth2User = oAuth2Client.fetchUser(request.getProvider(), request.getAuthorizationCode());

        OauthMember member = oAuthMemberManager.syncMember(request.getProvider(), oAuth2User);

        loginLockManager.validateMemberStatus(member);

        loginHistoryRecorder.save(member.getId(), ipAddress, userAgent);
        return tokenManager.issueTokens(member);
    }
}