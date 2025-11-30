package orinnetwork.jpstudy.domain.filtering.engine;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import orinnetwork.jpstudy.domain.filtering.KeywordType;

class AhoCorasickEngineTest {

    private AhoCorasickEngine engine;

    @BeforeEach
    void setUp() {
        TrieBuilder trieBuilder = new TrieBuilder();
        engine = new AhoCorasickEngine(trieBuilder);
    }

    @Test
    @DisplayName("기본적 키워드 탐지 테스트")
    void basicSearchTest() {
        Map<String, KeywordType> keywords = new HashMap<>();
        keywords.put("존나", KeywordType.PROFANITY);
        keywords.put("찐따", KeywordType.INSULT);
        keywords.put("좌빨", KeywordType.POLITICAL);

        engine.initialize(keywords);

        String text = "저 사람은 존나 어리숙하다";
        Set<KeywordType> result = engine.search(text);

        assertThat(result).contains(KeywordType.PROFANITY);
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("겹치는 키워드 탐지 테스트")
    void overlapSearchTest() {
        Map<String, KeywordType> keywords = new HashMap<>();
        keywords.put("존나", KeywordType.PROFANITY);
        keywords.put("찐따", KeywordType.INSULT);
        keywords.put("좌빨", KeywordType.POLITICAL);

        engine.initialize(keywords);

        String text = "존나 찐따같은 놈";
        Set<KeywordType> result = engine.search(text);

        assertThat(result).contains(KeywordType.PROFANITY, KeywordType.INSULT);
    }

    @Test
    @DisplayName("포함 관계 및 접미사 탐지 테스트")
    void failureLinkTest() {Map<String, KeywordType> keywords = new HashMap<>();
        keywords.put("시발", KeywordType.PROFANITY);
        keywords.put("시발점", KeywordType.POLITICAL);

        engine.initialize(keywords);

        String text = "사건의 시발점이다";
        Set<KeywordType> result = engine.search(text);

        assertThat(result).contains(KeywordType.PROFANITY, KeywordType.POLITICAL);
    }

    @Test
    @DisplayName("클린 텍스트 테스트")
    void noMatchTest() {
        Map<String, KeywordType> keywords = new HashMap<>();
        keywords.put("바보", KeywordType.INSULT);

        engine.initialize(keywords);

        String text = "나는 행복한 사람이다";
        Set<KeywordType> result = engine.search(text);

        assertThat(result).isEmpty();
    }
}