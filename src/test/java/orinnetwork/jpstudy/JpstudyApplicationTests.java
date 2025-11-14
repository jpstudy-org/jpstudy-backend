package orinnetwork.jpstudy;

import com.azure.communication.email.EmailClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import orinnetwork.jpstudy.infrastructure.email.EmailService;

@SpringBootTest
class JpstudyApplicationTests {

    @MockitoBean
    private EmailService emailService;

    @MockitoBean
    private EmailClient emailClient;

    @Test
    void contextLoads() {
    }

}
