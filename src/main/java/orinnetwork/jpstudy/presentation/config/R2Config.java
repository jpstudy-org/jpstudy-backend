package orinnetwork.jpstudy.presentation.config;

import java.net.URI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
public class R2Config {

    @Value("${cloudflare.r2.account-id}")
    private String accountId;
    @Value("${cloudflare.r2.access-key-id}")
    private String accessKeyId;
    @Value("${cloudflare.r2.secret-access-key}")
    private String secretAccessKey;

    @Bean
    public S3Presigner s3Presigner() {
        String endpoint = String.format("https://%s.r2.cloudflarestorage.com", accountId);

        return S3Presigner.builder()
                .region(Region.of("auto"))
                .endpointOverride(URI.create(endpoint))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKeyId, secretAccessKey))
                )
                .build();
    }
}