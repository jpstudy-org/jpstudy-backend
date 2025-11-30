package orinnetwork.jpstudy.application.auth.component;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import orinnetwork.jpstudy.domain.log.LoginHistory;
import orinnetwork.jpstudy.domain.log.LoginHistoryRepository;

@Component
@RequiredArgsConstructor
public class LoginHistoryRecorder {

    private final LoginHistoryRepository loginHistoryRepository;

    @Transactional
    public void save(Long memberId, String ipAddress, String userAgent) {
        LoginHistory history = LoginHistory.builder()
                .memberId(memberId)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .build();

        loginHistoryRepository.save(history);
    }
}
