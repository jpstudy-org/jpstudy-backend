package orinnetwork.jpstudy.domain.filtering.engine;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import orinnetwork.jpstudy.domain.filtering.KeywordType;

@Slf4j
@Component
@RequiredArgsConstructor
public class AhoCorasickEngine {

    private final TrieBuilder trieBuilder;
    private final AhoCorasickNode root = new AhoCorasickNode();

    public void initialize(Map<String, KeywordType> keywords) {
        trieBuilder.build(root, keywords);
        log.info("아호-코라식 엔진 초기화 완료.");
    }

    public Set<KeywordType> search(String text) {
        Set<KeywordType> foundTypes = new HashSet<>();
        AhoCorasickNode current = root;

        for (char ch : text.toCharArray()) {
            current = getNextNode(current, ch);
            collectKeywords(current, foundTypes);
        }
        return foundTypes;
    }

    private AhoCorasickNode getNextNode(AhoCorasickNode current, char ch) {
        while (current != null && !current.getChildren().containsKey(ch)) {
            current = current.getFailureLink();
        }
        if (current == null) {
            return root;
        }
        return current.getChildren().get(ch);
    }

    private void collectKeywords(AhoCorasickNode current, Set<KeywordType> foundTypes) {
        if (current.getOutputType().isEmpty()) {
            return;
        }
        foundTypes.addAll(current.getOutputType());
    }
}