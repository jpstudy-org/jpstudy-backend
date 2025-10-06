package orinnetwork.jpstudy.presentation.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app")
@Getter
@Setter
public class AppProperties {

    private final Frontend frontend = new Frontend();

    @Getter
    @Setter
    public static class Frontend {
        private String baseUri;
    }
}
