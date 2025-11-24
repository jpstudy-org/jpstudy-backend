package orinnetwork.jpstudy.application.auth.component;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import orinnetwork.jpstudy.infrastructure.email.EmailService;
import orinnetwork.jpstudy.infrastructure.exception.CustomException;
import orinnetwork.jpstudy.infrastructure.exception.ErrorCode;

@Component
@RequiredArgsConstructor
public class PasswordResetManager {

    private final EmailService emailService;
    private final RedisTemplate<String, String> authRedisTemplate;

    private static final String RESET_TOKEN_PREFIX = "RESET_TOKEN:";
    private static final String RESET_TOKEN_IDX_PREFIX = "RESET_TOKEN_IDX:"; // 이메일로 토큰 찾는 키
    private static final long EXPIRATION_MINUTES = 15;

    /**
     * 재설정 토큰 생성
     * @param email Email
     */
    public void sendResetLink(String email) {
        invalidateOldToken(email);

        String newToken = UUID.randomUUID().toString();

        saveToken(newToken, email);

        emailService.sendPasswordResetLink(email, newToken);
    }

    /**
     * 토큰 검증
     * @param token Token
     */
    public String validateAndGetEmail(String token) {
        String email = authRedisTemplate.opsForValue().get(RESET_TOKEN_PREFIX + token);
        if (email == null) {
            throw new CustomException(ErrorCode.INVALID_INPUT);
        }
        return email;
    }

    /**
     * 토큰 삭제
     * @param token Token
     */
    public void deleteToken(String token) {
        String email = authRedisTemplate.opsForValue().get(RESET_TOKEN_PREFIX + token);

        if (email != null) {
            authRedisTemplate.delete(RESET_TOKEN_IDX_PREFIX + email);
        }
        authRedisTemplate.delete(RESET_TOKEN_PREFIX + token);
    }


    // --- private ---

    private void invalidateOldToken(String email) {
        String oldToken = authRedisTemplate.opsForValue().get(RESET_TOKEN_IDX_PREFIX + email);
        if (oldToken != null) {
            authRedisTemplate.delete(RESET_TOKEN_PREFIX + oldToken);
            authRedisTemplate.delete(RESET_TOKEN_IDX_PREFIX + email);
        }
    }

    private void saveToken(String token, String email) {
        // Check to TOKEN
        authRedisTemplate.opsForValue().set(
                RESET_TOKEN_PREFIX + token,
                email,
                EXPIRATION_MINUTES,
                TimeUnit.MINUTES
        );

        // Search to Email
        authRedisTemplate.opsForValue().set(
                RESET_TOKEN_IDX_PREFIX + email,
                token,
                EXPIRATION_MINUTES,
                TimeUnit.MINUTES
        );
    }
}
