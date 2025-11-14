package orinnetwork.jpstudy;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import orinnetwork.jpstudy.infrastructure.email.EmailService;

@SpringBootTest
class JpstudyApplicationTests {

    @MockitoBean
    private EmailService emailService;

    @Test
    void contextLoads() {
    }

}
