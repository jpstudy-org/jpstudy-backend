package orinnetwork.jpstudy.infrastructure.filtering;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;
import orinnetwork.jpstudy.domain.filtering.engine.AhoCorasickEngine;
import orinnetwork.jpstudy.domain.filtering.KeywordType;

@Slf4j
@Component
@RequiredArgsConstructor
public class KeywordLoader {

    private final AhoCorasickEngine engine;
    private final ResourcePatternResolver resourcePatternResolver;
    private final ObjectMapper objectMapper;

    private record KeywordEntry(String word, KeywordType type) {}

    @PostConstruct
    public void loadKeyWords() {
        Map<String, KeywordType> finalKeywordMap = new HashMap<>();

        try {
            Resource[] resources = resourcePatternResolver.getResources("classpath:keywords/*.json");

            if (resources.length == 0) {
                log.warn("'classpath:keywords/' 폴더의 JSON 키워드 파일이 존재하지 않음");
                engine.initialize(Map.of());
                return;
            }

            for (Resource resource : resources) {
                try (InputStream inputStream = resource.getInputStream()) {
                    List<KeywordEntry> entries = objectMapper.readValue(
                            inputStream,
                            new TypeReference<List<KeywordEntry>>() {}
                    );

                    Map<String, KeywordType> mapFromfile = entries.stream()
                            .collect(Collectors.toMap(
                                    KeywordEntry::word,
                                    KeywordEntry::type,
                                    (existing, replacement) -> existing
                            ));

                    mapFromfile.forEach((word, type) ->
                            finalKeywordMap.merge(word, type, (oldType, newType) -> oldType)
                    );
                }
            }
            engine.initialize(finalKeywordMap);

        } catch (Exception e) {
            log.error("로드 또는 파싱에 실패했습니다. 필터 엔진을 초기화할 수 없습니다.");
            throw new RuntimeException("Failed to initialize AhoCorasickEngine");
        }
    }
}