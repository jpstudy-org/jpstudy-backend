package orinnetwork.jpstudy.application.auth.component;

import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import orinnetwork.jpstudy.application.auth.dto.TokenResponse;
import orinnetwork.jpstudy.domain.member.Member;
import orinnetwork.jpstudy.domain.member.MemberRepository;
import orinnetwork.jpstudy.infrastructure.exception.CustomException;
import orinnetwork.jpstudy.infrastructure.exception.ErrorCode;
import orinnetwork.jpstudy.infrastructure.jwt.JwtProvider;

@Component
@RequiredArgsConstructor
public class TokenManager {

    private final JwtProvider  jwtProvider;
    private final RedisTemplate<String, String> authRedisTemplate;
    private final MemberRepository memberRepository;

    private static final String REFRESH_TOKEN_PREFIX = "RT:";

    public TokenResponse issueTokens(Member member) {
        String accessToken = jwtProvider.createAccessToken(member.getId(), member.getRole());
        String refreshToken = jwtProvider.createRefreshToken(member.getId());
        long validity = jwtProvider.getRefreshTokenValidityInMilliseconds();

        saveRefreshTokenToRedis(member.getId(), refreshToken, validity);

        return new TokenResponse(accessToken, refreshToken, member.getUsername(), validity);
    }

    public TokenResponse reissue(String refreshToken) {
        if (!jwtProvider.isValidToken(refreshToken)) {
            throw new CustomException(ErrorCode.LOGIN_FAILED);
        }

        // Check to Redis
        Long memberId = jwtProvider.getUserId(refreshToken);
        String storedRefreshToken = getRefreshTokenFromRedis(memberId);

        if (storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)) {
            throw new CustomException(ErrorCode.LOGIN_FAILED);
        }

        // Find Member Role
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.ACCOUNT_DISABLED));


        // Create AccessToken
        String newAccessToken = jwtProvider.createAccessToken(member.getId(), member.getRole());
        long validity = jwtProvider.getRefreshTokenValidityInMilliseconds();

        return new TokenResponse(newAccessToken, refreshToken, member.getUsername(), validity);
    }

    public void deleteRefreshToken(Long memberId) {
        String redisKey = REFRESH_TOKEN_PREFIX + memberId;
        if (authRedisTemplate.opsForValue().get(redisKey) != null) {
            authRedisTemplate.delete(redisKey);
        }
    }

    private void saveRefreshTokenToRedis(Long memberId, String refreshToken, long validityMs) {
        String redisKey = REFRESH_TOKEN_PREFIX + memberId;
        authRedisTemplate.opsForValue().set(
                redisKey,
                refreshToken,
                validityMs,
                TimeUnit.MILLISECONDS
        );
    }

    private String getRefreshTokenFromRedis(Long memberId) {
        String redisKey = REFRESH_TOKEN_PREFIX + memberId;
        return authRedisTemplate.opsForValue().get(redisKey);
    }
}
