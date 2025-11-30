package orinnetwork.jpstudy.presentation.consumer;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import orinnetwork.jpstudy.application.filtering.FilterService;
import orinnetwork.jpstudy.infrastructure.config.RabbitConfig;
import orinnetwork.jpstudy.presentation.consumer.dto.PostEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class FilterConsumer {

    private final FilterService filterService;

    @RabbitListener(queues = RabbitConfig.FILTER_QUEUE_NAME)
    public void handleFilteringMessage(PostEvent eventDto) {

        if (eventDto == null || eventDto.postId() == null) {
            log.warn("수신된 메시지가 비어있습니다");
            return;
        }

        Long postId = eventDto.postId();
        log.info("필터링 요청 수신 : {}", postId);

        try {
            filterService.filterPost(postId);
        } catch (IllegalArgumentException e) {
            log.error("처리 불가능한 오류");
        } catch (Exception e) {
            throw new RuntimeException("메시지 처리 실패");
        }
    }
}