package orinnetwork.jpstudy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class JpstudyApplication {

    public static void main(String[] args) {
        SpringApplication.run(JpstudyApplication.class, args);
    }

}
