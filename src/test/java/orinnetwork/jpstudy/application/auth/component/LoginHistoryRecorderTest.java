package orinnetwork.jpstudy.application.auth.component;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import orinnetwork.jpstudy.domain.log.LoginHistory;
import orinnetwork.jpstudy.domain.log.LoginHistoryRepository;

@ExtendWith(MockitoExtension.class)
class LoginHistoryRecorderTest {

    @InjectMocks
    private LoginHistoryRecorder loginHistoryRecorder;

    @Mock
    private LoginHistoryRepository loginHistoryRepository;

    @Test
    @DisplayName("로그인 기록 저장")
    void save_success() {
        Long memberId = 1L;
        String ip = "127.0.0.1";
        String ua = "Chrome";

        given(loginHistoryRepository.save(any(LoginHistory.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        loginHistoryRecorder.save(memberId, ip, ua);

        then(loginHistoryRepository).should(times(1)).save(any(LoginHistory.class));
    }

}