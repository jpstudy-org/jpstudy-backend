package orinnetwork.jpstudy.application.auth.component;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

import java.util.Optional;
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
import orinnetwork.jpstudy.application.auth.dto.TokenResponse;
import orinnetwork.jpstudy.domain.member.Member;
import orinnetwork.jpstudy.domain.member.MemberRepository;
import orinnetwork.jpstudy.domain.member.Role;
import orinnetwork.jpstudy.infrastructure.exception.CustomException;
import orinnetwork.jpstudy.infrastructure.exception.ErrorCode;
import orinnetwork.jpstudy.infrastructure.jwt.JwtProvider;

@ExtendWith(MockitoExtension.class)
class TokenManagerTest {

    @InjectMocks
    private TokenManager tokenManager;

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private RedisTemplate<String, String> authRedisTemplate;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @BeforeEach
    void setUp() {
        lenient().when(authRedisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    @DisplayName("토큰 발급 시 Access/Refresh 토큰을 생성, Redis 저장")
    void issueTokens_success() {
        Member member = mock(Member.class);
        given(member.getId()).willReturn(1L);
        given(member.getRole()).willReturn(Role.USER);
        given(member.getUsername()).willReturn("Tester");

        given(jwtProvider.createAccessToken(anyLong(), any())).willReturn("access-token");
        given(jwtProvider.createRefreshToken(anyLong())).willReturn("refresh-token");
        given(jwtProvider.getRefreshTokenValidityInMilliseconds()).willReturn(10000L);

        TokenResponse response = tokenManager.issueTokens(member);

        assertThat(response.getAccessToken()).isEqualTo("access-token");
        assertThat(response.getRefreshToken()).isEqualTo("refresh-token");

        then(valueOperations).should().set(
                eq("RT:1"),
                eq("refresh-token"),
                eq(10000L),
                eq(TimeUnit.MILLISECONDS)
        );
    }


    @Test
    @DisplayName("유효한 리프레시 토큰 -> 액세스 토큰 재발급")
    void reissue_success() {
        String refreshToken = "valid-refresh-token";
        Long memberId = 1L;
        String redisKey = "RT:" + memberId;

        given(jwtProvider.isValidToken(refreshToken)).willReturn(true);
        given(jwtProvider.getUserId(refreshToken)).willReturn(memberId);

        given(valueOperations.get(redisKey)).willReturn(refreshToken);

        Member member = mock(Member.class);
        given(member.getId()).willReturn(memberId);
        given(member.getRole()).willReturn(Role.USER);
        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));

        given(jwtProvider.createAccessToken(memberId, Role.USER)).willReturn("new-access-token");
        given(jwtProvider.getRefreshTokenValidityInMilliseconds()).willReturn(10000L);

        TokenResponse response = tokenManager.reissue(refreshToken);

        assertThat(response.getAccessToken()).isEqualTo("new-access-token");
        assertThat(response.getRefreshToken()).isEqualTo(refreshToken); // 리프레시 토큰은 그대로 반환됨
    }

    @Test
    @DisplayName("유효하지 않은(만료/조작) 토큰 -> LOGIN_FAILED 예외 발생")
    void reissue_fail_invalid_token() {
        String refreshToken = "invalid-token";
        given(jwtProvider.isValidToken(refreshToken)).willReturn(false);

        CustomException ex = assertThrows(CustomException.class,
                () -> tokenManager.reissue(refreshToken));

        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.LOGIN_FAILED);
    }

    @Test
    @DisplayName("Redis에 저장된 토큰이 없음 -> LOGIN_FAILED 예외 발생")
    void reissue_fail_redis_miss() {
        String refreshToken = "valid-but-logged-out";
        Long memberId = 1L;

        given(jwtProvider.isValidToken(refreshToken)).willReturn(true);
        given(jwtProvider.getUserId(refreshToken)).willReturn(memberId);

        given(valueOperations.get("RT:1")).willReturn(null);

        CustomException ex = assertThrows(CustomException.class,
                () -> tokenManager.reissue(refreshToken));

        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.LOGIN_FAILED);
    }

    @Test
    @DisplayName("Redis 토큰과 요청 토큰이 다름 -> LOGIN_FAILED 예외가 발생")
    void reissue_fail_token_mismatch() {
        String requestToken = "token-A";
        String storedToken = "token-B";
        Long memberId = 1L;

        given(jwtProvider.isValidToken(requestToken)).willReturn(true);
        given(jwtProvider.getUserId(requestToken)).willReturn(memberId);

        given(valueOperations.get("RT:1")).willReturn(storedToken);

        CustomException ex = assertThrows(CustomException.class,
                () -> tokenManager.reissue(requestToken));

        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.LOGIN_FAILED);
    }


    @Test
    @DisplayName("토큰 삭제 요청 -> Redis에서 해당 키 삭제")
    void deleteRefreshToken_success() {
        Long memberId = 1L;
        String redisKey = "RT:1";

        given(valueOperations.get(redisKey)).willReturn("some-token");

        tokenManager.deleteRefreshToken(memberId);

        then(authRedisTemplate).should(times(1)).delete(redisKey);
    }
}