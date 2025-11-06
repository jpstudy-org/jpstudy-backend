package orinnetwork.jpstudy; // (프로젝트의 메인 패키지 경로)

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

/**
 * Jenkins/Test 환경을 위한 설정 클래스.
 * 실제 서버(Redis 등)에 연결하지 않고 Mock 객체를 Bean으로 등록합니다.
 */
@Configuration
public class TestConfig {

    /**
     * Jenkins 환경에서 Redis 서버 없이도 'RedisTemplate' Bean을
     * 주입할 수 있도록 가짜(Mock) 객체를 생성합니다.
     */
    @Bean
    @Primary // 다른 RedisTemplate Bean이 있어도, 이 가짜 Bean을 최우선으로 사용
    @SuppressWarnings("unchecked") // Mockito 제네릭 경고 무시
    public RedisTemplate<String, String> mockRedisTemplate() {
        RedisTemplate<String, String> mockTemplate = Mockito.mock(RedisTemplate.class);

        ValueOperations<String, String> mockOps = Mockito.mock(ValueOperations.class);
        Mockito.when(mockTemplate.opsForValue()).thenReturn(mockOps);

        return mockTemplate;
    }
}