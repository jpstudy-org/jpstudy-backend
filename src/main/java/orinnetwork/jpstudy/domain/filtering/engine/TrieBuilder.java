package orinnetwork.jpstudy.domain.filtering.engine;

import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import org.springframework.stereotype.Component;
import orinnetwork.jpstudy.domain.filtering.KeywordType;

@Component
public class TrieBuilder {

    public void build(AhoCorasickNode root, Map<String, KeywordType> keywords) {
        if (keywords == null) return;
        keywords.forEach((key, value) -> insert(root, key, value));
        createFailureLinks(root);
    }

    private void insert(AhoCorasickNode root, String pattern, KeywordType type) {
        AhoCorasickNode current = root;
        for (char ch : pattern.toCharArray()) {
            current = current.getChildren().computeIfAbsent(ch, c -> new AhoCorasickNode());
        }
        current.addOutputType(type);
    }

    private void createFailureLinks(AhoCorasickNode root) {
        Queue<AhoCorasickNode> queue = new LinkedList<>();
        initializeRootChildren(root, queue);
        processQueue(root, queue);
    }

    private void initializeRootChildren(AhoCorasickNode root, Queue<AhoCorasickNode> queue) {
        for (AhoCorasickNode child : root.getChildren().values()) {
            child.setFailureLink(root);
            queue.add(child);
        }
    }

    private void processQueue(AhoCorasickNode root, Queue<AhoCorasickNode> queue) {
        while (!queue.isEmpty()) {
            processNode(root, queue.poll(), queue);
        }
    }

    private void processNode(AhoCorasickNode root, AhoCorasickNode parent, Queue<AhoCorasickNode> queue) {
        for (Entry<Character, AhoCorasickNode> entry : parent.getChildren().entrySet()) {
            processChild(root, parent, entry, queue);
        }
    }

    private void processChild(AhoCorasickNode root, AhoCorasickNode parent,
                              Entry<Character, AhoCorasickNode> entry, Queue<AhoCorasickNode> queue) {
        char ch = entry.getKey();
        AhoCorasickNode child = entry.getValue();
        AhoCorasickNode fail = resolveFailureNode(parent.getFailureLink(), ch);

        child.setFailureLink(fail == null ? root : fail.getChildren().get(ch));
        if (child.getFailureLink() != null) { // NPE 방지 및 로직 단순화
            child.addOutputTypes(child.getFailureLink().getOutputType());
        }
        queue.add(child);
    }

    private AhoCorasickNode resolveFailureNode(AhoCorasickNode fail, char ch) {
        while (fail != null && !fail.getChildren().containsKey(ch)) {
            fail = fail.getFailureLink();
        }
        return fail;
    }
}