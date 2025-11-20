package orinnetwork.jpstudy.application.auth;

import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import orinnetwork.jpstudy.application.auth.dto.LoginRequest;
import orinnetwork.jpstudy.application.auth.dto.TokenResponse;
import orinnetwork.jpstudy.domain.member.Member;
import orinnetwork.jpstudy.domain.member.MemberRepository;
import orinnetwork.jpstudy.domain.member.Role;
import orinnetwork.jpstudy.infrastructure.exception.CustomException;
import orinnetwork.jpstudy.infrastructure.exception.ErrorCode;
import orinnetwork.jpstudy.infrastructure.jwt.JwtProvider;
import orinnetwork.jpstudy.infrastructure.security.CustomUserDetails;

@Service
@RequiredArgsConstructor
public class AdminAuthservice {

    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;
    private final AuthenticationManager authenticationManager;

    private final RedisTemplate<String, String> authRedisTemplate;
    private static final String REFRESH_TOKEN_PREFIX = "RT:";

    private static final String LOGIN_FAIL_PREFIX = "ADMIN_LOGIN_FAIL:"; // 관리자 전용 Prefix
    private static final int MAX_LOGIN_ATTEMPTS = 5;
    private static final long LOCKOUT_DURATION_MINUTES = 10;

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
        Long memberId = userDetails.getMemberId();
        Role role = userDetails.getMember().getRole();
        String userName = userDetails.getUsername();

        if (role != Role.ADMIN) {
            throw new CustomException(ErrorCode.NOT_ADMIN);
        }

        String accessToken = jwtProvider.createAccessToken(memberId, role);
        String refreshToken = jwtProvider.createRefreshToken(memberId);

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

        if (member.getRole() != Role.ADMIN) {
            throw new CustomException(ErrorCode.NOT_ADMIN);
        }

        String newAccessToken = jwtProvider.createAccessToken(member.getId(), member.getRole());
        String userName = member.getUsername();
        long refreshTokenValidityMs = jwtProvider.getRefreshTokenValidityInMilliseconds();

        return new TokenResponse(newAccessToken, clientRefreshToken, userName, refreshTokenValidityMs);
    }
}
