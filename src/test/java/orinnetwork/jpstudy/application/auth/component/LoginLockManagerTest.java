package orinnetwork.jpstudy.application.auth.component;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

import java.time.LocalDateTime;
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
import orinnetwork.jpstudy.domain.member.Member;
import orinnetwork.jpstudy.domain.member.MemberStatus;
import orinnetwork.jpstudy.infrastructure.exception.CustomException;
import orinnetwork.jpstudy.infrastructure.exception.ErrorCode;

@ExtendWith(MockitoExtension.class)
class LoginLockManagerTest {

    @InjectMocks
    private LoginLockManager loginLockManager;

    @Mock
    private RedisTemplate<String, String> authRedisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    private static final String EMAIL = "test@test.com";
    private static final String REDIS_KEY = "LOGIN_FAIL:" + EMAIL;

    @BeforeEach
    void setUp() {
        lenient().when(authRedisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    @DisplayName("로그인 실패 기록 없음 - 통과")
    void validateNotLocked_success_no_record() {
        given(valueOperations.get(REDIS_KEY)).willReturn(null);

        assertDoesNotThrow(() -> loginLockManager.validateNotLocked(EMAIL));
    }

    @Test
    @DisplayName("로그인 실패 기록 4 - 통과")
    void validateNotLocked_success_under_limit() {
        given(valueOperations.get(REDIS_KEY)).willReturn("4");

        assertDoesNotThrow(() -> loginLockManager.validateNotLocked(EMAIL));
    }

    @Test
    @DisplayName("로그인 실패 기록 5 - 실패")
    void validateNotLocked_fail_locked() {
        given(valueOperations.get(REDIS_KEY)).willReturn("5");
        given(authRedisTemplate.getExpire(REDIS_KEY, TimeUnit.MINUTES)).willReturn(8L);

        CustomException exception = assertThrows(CustomException.class,
                () -> loginLockManager.validateNotLocked(EMAIL));

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.ACCOUNT_LOCKED);
        assertThat(exception.getMessage()).contains("9");
    }

    @Test
    @DisplayName("정상 회원 - 성공")
    void validateMemberStatus_success() {
        Member member = mock(Member.class);
        given(member.getDeletedAt()).willReturn(null);
        given(member.isAccountNonLocked()).willReturn(true);

        assertDoesNotThrow(() -> loginLockManager.validateMemberStatus(member));
    }

    @Test
    @DisplayName("탈퇴 회원 - 예외")
    void validateMemberStatus_fail_deleted() {
        Member member = mock(Member.class);
        given(member.getDeletedAt()).willReturn(LocalDateTime.now());

        CustomException ex = assertThrows(CustomException.class,
                () -> loginLockManager.validateMemberStatus(member));
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.ACCOUNT_DISABLED);
    }

    @Test
    @DisplayName("정지 회원 - 예외")
    void validateMemberStatus_fail_suspended() {
        Member member = mock(Member.class);
        given(member.getDeletedAt()).willReturn(null);
        given(member.isAccountNonLocked()).willReturn(false);
        given(member.getStatus()).willReturn(MemberStatus.SUSPENDED);
        given(member.getBanExpiresAt()).willReturn(LocalDateTime.now().plusMinutes(30));

        CustomException ex = assertThrows(CustomException.class,
                () -> loginLockManager.validateMemberStatus(member));

        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.ACCOUNT_LOCKED);
    }


    @Test
    @DisplayName("로그인 실패 시 - 카운팅")
    void increaseFailureCount_first_fail() {
        given(valueOperations.increment(REDIS_KEY)).willReturn(1L);

        loginLockManager.increaseFailureCount(EMAIL);

        then(valueOperations).should().increment(REDIS_KEY);
        then(authRedisTemplate).should().expire(eq(REDIS_KEY), eq(10L), eq(TimeUnit.MINUTES));
    }

    @Test
    @DisplayName("로그인 실패 5회 - 예외")
    void increaseFailureCount_fifth_fail() {
        given(valueOperations.increment(REDIS_KEY)).willReturn(5L);

        CustomException ex = assertThrows(CustomException.class,
                () -> loginLockManager.increaseFailureCount(EMAIL));

        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.ACCOUNT_LOCKED);
        then(authRedisTemplate).should(never()).expire(anyString(), anyLong(), any());
    }

    @Test
    @DisplayName("로그인 성공 시 - 기록 초기화")
    void resetFailureCount_success() {
        given(valueOperations.get(REDIS_KEY)).willReturn("2");

        loginLockManager.resetFailureCount(EMAIL);

        then(authRedisTemplate).should().delete(REDIS_KEY);
    }
}