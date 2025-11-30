package orinnetwork.jpstudy.application.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import orinnetwork.jpstudy.application.auth.component.LoginLockManager;
import orinnetwork.jpstudy.application.auth.component.TokenManager;
import orinnetwork.jpstudy.application.auth.dto.LoginRequest;
import orinnetwork.jpstudy.application.auth.dto.TokenResponse;
import orinnetwork.jpstudy.domain.member.Member;
import orinnetwork.jpstudy.domain.member.Role;
import orinnetwork.jpstudy.infrastructure.exception.CustomException;
import orinnetwork.jpstudy.infrastructure.exception.ErrorCode;
import orinnetwork.jpstudy.infrastructure.security.CustomUserDetails;

@Service
@RequiredArgsConstructor
public class AdminAuthservice {

    private final AuthenticationManager authenticationManager;
    private final TokenManager tokenManager;
    private final LoginLockManager loginLockManager;

    public TokenResponse login(LoginRequest request, String ip, String ua) {
        loginLockManager.validateNotLocked(request.getEmail());

        Member member = authenticateAdmin(request);

        loginLockManager.resetFailureCount(request.getEmail());

        return tokenManager.issueTokens(member);
    }

    public void logout() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.getPrincipal() instanceof CustomUserDetails userDetails) {
            tokenManager.deleteRefreshToken(userDetails.getMemberId());
        }
    }

    public TokenResponse reissueToken(String clientRefreshToken) {
        return tokenManager.reissue(clientRefreshToken);
    }

    // --- Private Helpers ---

    private Member authenticateAdmin(LoginRequest request) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
            Member member = ((CustomUserDetails) auth.getPrincipal()).getMember();

            validateAdminRole(member);

            return member;
        } catch (BadCredentialsException e) {
            loginLockManager.increaseFailureCount(request.getEmail());
            throw new CustomException(ErrorCode.LOGIN_FAILED);
        }
    }

    private void validateAdminRole(Member member) {
        if (member.getRole() != Role.ADMIN) {
            throw new CustomException(ErrorCode.NOT_ADMIN);
        }
    }
}
