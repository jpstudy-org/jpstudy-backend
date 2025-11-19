package orinnetwork.jpstudy.domain.log;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "login_history")
public class LoginHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long memberId;

    @Column(length = 50)
    private String ipAddress;
    private String userAgent;
    private LocalDateTime loginAt;

    @Builder
    public LoginHistory(Long memberId, String ipAddress, String userAgent) {
        this.memberId = memberId;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.loginAt = LocalDateTime.now();
    }
}
