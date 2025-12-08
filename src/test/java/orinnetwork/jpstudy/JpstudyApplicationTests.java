package orinnetwork.jpstudy;

import com.azure.communication.email.EmailClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import orinnetwork.jpstudy.infrastructure.email.EmailService;
import orinnetwork.jpstudy.infrastructure.notification.NotificationRedisSubscriber;

@SpringBootTest
@ActiveProfiles("test")
class JpstudyApplicationTests {

    @MockitoBean
    private EmailService emailService;

    @MockitoBean
    private EmailClient emailClient;

    @MockitoBean
    private NotificationRedisSubscriber notificationRedisSubscriber;

    @MockitoBean(name = "notificationRedisTemplate")
    private RedisTemplate<String, Object> notificationRedisTemplate;

    @MockitoBean(name = "authRedisTemplate")
    private RedisTemplate<String, String> authRedisTemplate;

    @MockitoBean
    private StringRedisTemplate stringRedisTemplate;

    @Test
    void contextLoads() {
    }

}
