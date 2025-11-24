package orinnetwork.jpstudy.application.auth;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import orinnetwork.jpstudy.application.auth.component.LoginHistoryRecorder;
import orinnetwork.jpstudy.application.auth.component.LoginLockManager;
import orinnetwork.jpstudy.application.auth.component.PasswordResetManager;
import orinnetwork.jpstudy.application.auth.component.TokenManager;
import orinnetwork.jpstudy.application.auth.dto.LoginRequest;
import orinnetwork.jpstudy.application.auth.dto.PasswordResetConfirm;
import orinnetwork.jpstudy.application.auth.dto.PasswordResetRequest;
import orinnetwork.jpstudy.application.auth.dto.SignUpRequest;
import orinnetwork.jpstudy.application.auth.dto.TokenResponse;
import orinnetwork.jpstudy.application.notification.NotificationService;
import orinnetwork.jpstudy.domain.member.LocalMember;
import orinnetwork.jpstudy.domain.member.Member;
import orinnetwork.jpstudy.domain.member.MemberRepository;
import orinnetwork.jpstudy.domain.member.Role;
import orinnetwork.jpstudy.domain.member.UsernameValidator;
import orinnetwork.jpstudy.domain.notification.NotificationMessage;
import orinnetwork.jpstudy.domain.notification.NotificationType;
import orinnetwork.jpstudy.infrastructure.exception.CustomException;
import orinnetwork.jpstudy.infrastructure.exception.ErrorCode;
import orinnetwork.jpstudy.infrastructure.security.CustomUserDetails;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UsernameValidator usernameValidator;
    private final NotificationService notificationService;

    private final LoginHistoryRecorder loginHistoryRecorder;
    private final LoginLockManager loginLockManager;
    private final PasswordResetManager passwordResetManager;
    private final TokenManager tokenManager;

    /**
     * 회원가입
     */
    @Transactional
    public TokenResponse signUp(SignUpRequest request, String ipAddress, String userAgent) {

        usernameValidator.validate(request.getUsername());

        final Member savedMember = createAndSaveMember(request);

        loginHistoryRecorder.save(savedMember.getId(), ipAddress, userAgent);
        sendWelcomeNotification(savedMember);

        return tokenManager.issueTokens(savedMember);
    }

    /**
     * 로그인
     */
    public TokenResponse login(LoginRequest request, String ipAddress, String userAgent) {
        loginLockManager.validateNotLocked(request.getEmail());

        Member member = authenticateUser(request);

        loginLockManager.validateMemberStatus(member);

        loginLockManager.resetFailureCount(request.getEmail());
        loginHistoryRecorder.save(member.getId(), ipAddress, userAgent);

        return tokenManager.issueTokens(member);
    }

    /**
     * 로그아웃
     */
    public void logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            tokenManager.deleteRefreshToken(userDetails.getMemberId());
        }
    }

    /**
     * 액세스 토큰 재발급
     */
    public TokenResponse reissueToken(String clientRefreshToken) {
        return tokenManager.reissue(clientRefreshToken);
    }

    /**
     * 비밀번호 초기화 요청
     */
    public void requestPasswordReset(PasswordResetRequest request) {
        Member member = getMemberByEmail(request.getEmail());
        passwordResetManager.sendResetLink(member.getEmail());
    }


    /**
     * 비밀번호 변경
     */
    @Transactional
    public void confirmPasswordReset(PasswordResetConfirm request) {
        String email = passwordResetManager.validateAndGetEmail(request.getToken());
        Member member = getMemberByEmail(email);

        if (member instanceof LocalMember localMember) {
            localMember.updatePassword(passwordEncoder.encode(request.getNewPassword()));
        }

        passwordResetManager.deleteToken(request.getToken());
        loginLockManager.resetFailureCount(email);
    }

    // --- private Helper ---

    private Member createAndSaveMember(SignUpRequest request) {
        return memberRepository.save(new LocalMember(
                request.getEmail(),
                request.getUsername(),
                passwordEncoder.encode(request.getPassword()),
                Role.USER
        ));
    }

    private void sendWelcomeNotification(Member member) {
        notificationService.send(
                member.getId(),
                NotificationType.SIGNUP,
                NotificationMessage.SIGNUP_WELCOME,
                member.getLanguagePreference(),
                ""
        );
    }

    private Member authenticateUser(LoginRequest request) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
            return ((CustomUserDetails) auth.getPrincipal()).getMember();
        } catch (BadCredentialsException e) {
            loginLockManager.increaseFailureCount(request.getEmail());
            throw new CustomException(ErrorCode.LOGIN_FAILED);
        }
    }

    private Member getMemberByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_INPUT));
    }
}