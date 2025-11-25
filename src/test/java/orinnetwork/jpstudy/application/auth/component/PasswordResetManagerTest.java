package orinnetwork.jpstudy.application.auth.component;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import orinnetwork.jpstudy.infrastructure.email.EmailService;
import orinnetwork.jpstudy.infrastructure.exception.CustomException;
import orinnetwork.jpstudy.infrastructure.exception.ErrorCode;

@ExtendWith(MockitoExtension.class)
class PasswordResetManagerTest {

    @InjectMocks
    private PasswordResetManager passwordResetManager;

    @Mock
    private EmailService emailService;

    @Mock
    private RedisTemplate<String, String> authRedisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @BeforeEach
    void setUp() {
        lenient().when(authRedisTemplate.opsForValue()).thenReturn(valueOperations);
    }


    @Test
    @DisplayName("새 토큰을 생성, 저장하고 이메일을 발송")
    void sendResetLink_success_new() {
        String email = "test@test.com";
        String idxKey = "RESET_TOKEN_IDX:" + email;

        given(valueOperations.get(idxKey)).willReturn(null);

        passwordResetManager.sendResetLink(email);

        then(authRedisTemplate).should(never()).delete(anyString());

        then(valueOperations).should(times(2)).set(
                anyString(),
                anyString(),
                eq(15L),
                eq(TimeUnit.MINUTES)
        );

        then(emailService).should().sendPasswordResetLink(eq(email), anyString());
    }

    @Test
    @DisplayName("이전 토큰 삭제 후 새 토큰을 발송")
    void sendResetLink_success_invalidate_old() {
        String email = "test@test.com";
        String idxKey = "RESET_TOKEN_IDX:" + email;
        String oldToken = "old-uuid-token";
        String oldTokenKey = "RESET_TOKEN:" + oldToken;

        given(valueOperations.get(idxKey)).willReturn(oldToken);

        passwordResetManager.sendResetLink(email);

        then(authRedisTemplate).should().delete(oldTokenKey);
        then(authRedisTemplate).should().delete(idxKey);

        then(valueOperations).should(times(2)).set(anyString(), anyString(), anyLong(), any());

        then(emailService).should().sendPasswordResetLink(eq(email), anyString());
    }

    @Test
    @DisplayName("유효한 토큰이면 이메일 반환")
    void validateAndGetEmail_success() {
        String token = "valid-token";
        String email = "test@test.com";
        String tokenKey = "RESET_TOKEN:" + token;

        given(valueOperations.get(tokenKey)).willReturn(email);

        String result = passwordResetManager.validateAndGetEmail(token);

        assertThat(result).isEqualTo(email);
    }

    @Test
    @DisplayName("만료되거나 없는 토큰이면 예외 발생")
    void validateAndGetEmail_fail_invalid() {
        String token = "invalid-token";
        String tokenKey = "RESET_TOKEN:" + token;

        given(valueOperations.get(tokenKey)).willReturn(null);

        CustomException ex = assertThrows(CustomException.class,
                () -> passwordResetManager.validateAndGetEmail(token));

        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.INVALID_INPUT);
    }


    @Test
    @DisplayName("토큰 삭제 시 인덱스도 함께 삭제")
    void deleteToken_success() {
        String token = "used-token";
        String email = "test@test.com";
        String tokenKey = "RESET_TOKEN:" + token;
        String idxKey = "RESET_TOKEN_IDX:" + email;

        given(valueOperations.get(tokenKey)).willReturn(email);

        passwordResetManager.deleteToken(token);

        then(authRedisTemplate).should().delete(idxKey);
        then(authRedisTemplate).should().delete(tokenKey);
    }
}