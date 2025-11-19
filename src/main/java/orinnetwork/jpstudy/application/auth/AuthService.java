package orinnetwork.jpstudy.application.auth;

import jakarta.transaction.Transactional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import orinnetwork.jpstudy.application.auth.dto.LoginRequest;
import orinnetwork.jpstudy.application.auth.dto.PasswordResetConfirm;
import orinnetwork.jpstudy.application.auth.dto.PasswordResetRequest;
import orinnetwork.jpstudy.application.auth.dto.SignUpRequest;
import orinnetwork.jpstudy.application.auth.dto.TokenResponse;
import orinnetwork.jpstudy.application.notification.NotificationService;
import orinnetwork.jpstudy.domain.log.LoginHistory;
import orinnetwork.jpstudy.domain.log.LoginHistoryRepository;
import orinnetwork.jpstudy.domain.member.LocalMember;
import orinnetwork.jpstudy.domain.member.Member;
import orinnetwork.jpstudy.domain.member.Role;
import orinnetwork.jpstudy.domain.member.UsernameValidator;
import orinnetwork.jpstudy.domain.notification.NotificationMessage;
import orinnetwork.jpstudy.domain.notification.NotificationType;
import orinnetwork.jpstudy.infrastructure.email.EmailService;
import orinnetwork.jpstudy.infrastructure.exception.CustomException;
import orinnetwork.jpstudy.infrastructure.exception.ErrorCode;
import orinnetwork.jpstudy.infrastructure.jwt.JwtProvider;
import orinnetwork.jpstudy.domain.member.MemberRepository;
import orinnetwork.jpstudy.infrastructure.security.CustomUserDetails;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final AuthenticationManager authenticationManager;
    private final UsernameValidator usernameValidator;

    private final RedisTemplate<String, String> authRedisTemplate;
    private static final String REFRESH_TOKEN_PREFIX = "RT:";

    private final NotificationService notificationService;

    private final EmailService emailService;
    private static final String RESET_TOKEN_PREFIX = "RESET_TOKEN:";
    private static final long RESET_TOKEN_EXPIRATION_MINUTES = 15;

    private static final String LOGIN_FAIL_PREFIX = "LOGIN_FAIL:";
    private static final int MAX_LOGIN_ATTEMPTS = 5;
    private static final long LOCKOUT_DURATION_MINUTES = 10;
    private final LoginHistoryRepository loginHistoryRepository;

    public TokenResponse signUp(SignUpRequest requestDto, String ipAddress, String userAgent) {

        usernameValidator.validate(requestDto.getUsername());

        final String encryptedPassword = passwordEncoder.encode(requestDto.getPassword());
        final LocalMember newMember = new LocalMember(
                requestDto.getEmail(),
                requestDto.getUsername(),
                encryptedPassword,
                Role.USER
        );

        final Member savedMember = memberRepository.save(newMember);

        String accessToken = jwtProvider.createAccessToken(savedMember.getId(), savedMember.getRole());
        String refreshToken = jwtProvider.createRefreshToken(savedMember.getId());

        String userName = savedMember.getUsername();

        LoginHistory loginHistory = LoginHistory.builder()
                .memberId(savedMember.getId())
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .build();

        loginHistoryRepository.save(loginHistory);

        String redisKey = REFRESH_TOKEN_PREFIX + savedMember.getId().toString();
        long refreshTokenValidityMs = jwtProvider.getRefreshTokenValidityInMilliseconds();

        authRedisTemplate.opsForValue().set(
                redisKey,
                refreshToken,
                refreshTokenValidityMs,
                TimeUnit.MILLISECONDS
        );

        notificationService.send(
                savedMember.getId(),
                NotificationType.SIGNUP,
                NotificationMessage.SIGNUP_WELCOME,
                savedMember.getLanguagePreference(),
                ""
        );

        return new TokenResponse(accessToken, refreshToken, userName, refreshTokenValidityMs);
    }

    public TokenResponse login(LoginRequest requestDto, String ipAddress, String userAgent) {

        String lockoutKey = LOGIN_FAIL_PREFIX + requestDto.getEmail();
        String currentFailCountStr = authRedisTemplate.opsForValue().get(lockoutKey);

        if (currentFailCountStr != null) {
            int failCount = Integer.parseInt(currentFailCountStr);
            if (failCount >= MAX_LOGIN_ATTEMPTS) {
                long expireTimeMinutes = authRedisTemplate.getExpire(lockoutKey, TimeUnit.MINUTES);
                long remainTime = expireTimeMinutes > 0 ? expireTimeMinutes + 1 : LOCKOUT_DURATION_MINUTES;

                throw new CustomException(ErrorCode.ACCOUNT_LOCKED, remainTime);
            }
        }

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(requestDto.getEmail(), requestDto.getPassword());

        Authentication authentication;

        try {
            authentication = authenticationManager.authenticate(authenticationToken);
        } catch (BadCredentialsException e) {
            Long newFailCount = authRedisTemplate.opsForValue().increment(lockoutKey);

            if (newFailCount != null && newFailCount == 1) {
                authRedisTemplate.expire(lockoutKey, LOCKOUT_DURATION_MINUTES, TimeUnit.MINUTES);
            }
            if (newFailCount != null && newFailCount >= MAX_LOGIN_ATTEMPTS) {
                throw new CustomException(ErrorCode.ACCOUNT_LOCKED, LOCKOUT_DURATION_MINUTES);
            } else {
                throw new CustomException(ErrorCode.LOGIN_FAILED);
            }
        }

        if (currentFailCountStr != null) {
            authRedisTemplate.delete(lockoutKey);
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        if (!userDetails.isEnabled()) {
            throw new CustomException(ErrorCode.ACCOUNT_DISABLED);
        }

        Long memberId = userDetails.getMemberId();
        Role role = userDetails.getMember().getRole();
        String userName = userDetails.getUsername();

        String accessToken = jwtProvider.createAccessToken(memberId, role);
        String refreshToken = jwtProvider.createRefreshToken(memberId);

        LoginHistory loginHistory = LoginHistory.builder()
                .memberId(memberId)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .build();

        loginHistoryRepository.save(loginHistory);

        String redisKey = REFRESH_TOKEN_PREFIX + memberId;
        long refreshTokenValidityMs = jwtProvider.getRefreshTokenValidityInMilliseconds();

        authRedisTemplate.opsForValue().set(
                redisKey,
                refreshToken,
                refreshTokenValidityMs,
                TimeUnit.MILLISECONDS
        );

        return new TokenResponse(accessToken, refreshToken, userName, refreshTokenValidityMs);
    }

    public void logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails userDetails)) {
            return;
        }

        Long memberId = userDetails.getMemberId();

        String redisKey = REFRESH_TOKEN_PREFIX + memberId.toString();
        if (authRedisTemplate.opsForValue().get(redisKey) != null) {
            authRedisTemplate.delete(redisKey);
        }
    }

    public TokenResponse reissueToken(String clientRefreshToken) {
        if (!jwtProvider.isValidToken(clientRefreshToken)) {
            throw new CustomException(ErrorCode.LOGIN_FAILED);
        }

        Long memberId = jwtProvider.getUserId(clientRefreshToken);

        String redisKey = REFRESH_TOKEN_PREFIX + memberId.toString();
        String storedRefreshToken = authRedisTemplate.opsForValue().get(redisKey);

        if (storedRefreshToken == null) {
            throw new CustomException(ErrorCode.LOGIN_FAILED);
        }

        if (!storedRefreshToken.equals(clientRefreshToken)) {
            throw new CustomException(ErrorCode.LOGIN_FAILED);
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.ACCOUNT_DISABLED));

        String newAccessToken = jwtProvider.createAccessToken(member.getId(), member.getRole());
        String userName = member.getUsername();
        long refreshTokenValidityMs = jwtProvider.getRefreshTokenValidityInMilliseconds();

        return new TokenResponse(newAccessToken, clientRefreshToken, userName, refreshTokenValidityMs);
    }

    public void requestPasswordReset(PasswordResetRequest request) {
        Member member = memberRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_INPUT));

        String resetToken = UUID.randomUUID().toString();
        String redisKey = RESET_TOKEN_PREFIX + resetToken;

        authRedisTemplate.opsForValue().set(
                redisKey,
                member.getEmail(),
                RESET_TOKEN_EXPIRATION_MINUTES,
                TimeUnit.MINUTES
        );

        emailService.sendPasswordResetLink(member.getEmail(), resetToken);
    }

    public void confirmPasswordReset(PasswordResetConfirm request) {
        String redisKey = RESET_TOKEN_PREFIX + request.getToken();

        String userEmail = authRedisTemplate.opsForValue().get(redisKey);

        if (userEmail == null) {
            throw new CustomException(ErrorCode.INVALID_INPUT);
        }

        Member member = memberRepository.findByEmail(userEmail)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_INPUT));

        if (member instanceof LocalMember localMember) {
            localMember.updatePassword(passwordEncoder.encode(request.getNewPassword()));
        } else {
            throw new CustomException(ErrorCode.INVALID_INPUT);
        }

            String lockoutKey = LOGIN_FAIL_PREFIX + userEmail;
            authRedisTemplate.delete(lockoutKey);
            authRedisTemplate.delete(redisKey);
    }
}
