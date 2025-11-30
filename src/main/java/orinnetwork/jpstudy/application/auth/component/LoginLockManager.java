package orinnetwork.jpstudy.application.auth.component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import orinnetwork.jpstudy.domain.member.Member;
import orinnetwork.jpstudy.domain.member.MemberStatus;
import orinnetwork.jpstudy.infrastructure.exception.CustomException;
import orinnetwork.jpstudy.infrastructure.exception.ErrorCode;

@Component
@RequiredArgsConstructor
public class LoginLockManager {

    private final RedisTemplate<String, String> authRedisTemplate;

    private static final String LOGIN_FAIL_PREFIX = "LOGIN_FAIL:";
    private static final int MAX_LOGIN_ATTEMPTS = 5;
    private static final long LOCKOUT_DURATION_MINUTES = 10;
    private static final long PERMANENT_BAN_MINUTES = 999999;

    /**
     * 로그인 시도 전, 계정이 잠겨있는 지 확인
     * @param email Email
     */
    public void validateNotLocked(String email) {
        String key = getKey(email);
        String currentFailCountStr = authRedisTemplate.opsForValue().get(key);

        if (currentFailCountStr != null && Integer.parseInt(currentFailCountStr) >= MAX_LOGIN_ATTEMPTS) {
            long expireTime = authRedisTemplate.getExpire(key, TimeUnit.MINUTES);
            long remainTime = expireTime > 0 ? expireTime + 1 : LOCKOUT_DURATION_MINUTES;

            throw new CustomException(ErrorCode.ACCOUNT_LOCKED, remainTime);
        }
    }

    /**
     * 로그인 시 계정 상태 확인 (정지/탈퇴)
     * @param member Member
     */
    public void validateMemberStatus(Member member) {
        if (member.getDeletedAt() != null) {
            throw new CustomException(ErrorCode.ACCOUNT_DISABLED);
        }

        if (!member.isAccountNonLocked()) {
            long remainMinutes = calculateBanTime(member);
            throw new CustomException(ErrorCode.ACCOUNT_LOCKED, remainMinutes);
        }
    }

    /**
     * 로그인 실패 시 카운트 증가 및 잠금 처리
     * @param email Email
     */
    public void increaseFailureCount(String email) {
        String key = getKey(email);
        Long newFailCount = authRedisTemplate.opsForValue().increment(key);

        if (newFailCount != null && newFailCount == 1) {
            authRedisTemplate.expire(key, LOCKOUT_DURATION_MINUTES, TimeUnit.MINUTES);
        }

        if (newFailCount != null && newFailCount >= MAX_LOGIN_ATTEMPTS) {
            throw new CustomException(ErrorCode.ACCOUNT_LOCKED, LOCKOUT_DURATION_MINUTES);
        }
    }


    /**
     * 로그인 성공 시 실패 카운트 초기화
     * @param email Email
     */
    public void resetFailureCount(String email) {
        String key = getKey(email);
        if (authRedisTemplate.opsForValue().get(key) != null) {
            authRedisTemplate.delete(key);
        }
    }

    private String getKey(String email) {
        return LOGIN_FAIL_PREFIX + email;
    }

    private long calculateBanTime(Member member) {
        if (member.getStatus() == MemberStatus.SUSPENDED && member.getBanExpiresAt() != null) {
            long minutes = Duration.between(LocalDateTime.now(), member.getBanExpiresAt()).toMinutes();
            return Math.max(1, minutes + 1); // 최소 1분 표시
        }
        return PERMANENT_BAN_MINUTES; // 영구 정지
    }
}