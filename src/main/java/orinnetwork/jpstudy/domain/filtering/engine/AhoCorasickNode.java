package orinnetwork.jpstudy.domain.filtering.engine;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import lombok.Getter;
import orinnetwork.jpstudy.domain.filtering.KeywordType;

public class AhoCorasickNode {

    @Getter
    private final Map<Character, AhoCorasickNode> children = new HashMap<>();

    @Getter
    private AhoCorasickNode failureLink;

    private final Set<KeywordType> outputTypes = new HashSet<>();

    public void setFailureLink(AhoCorasickNode failureLink) {
        this.failureLink = failureLink;
    }

    public Set<KeywordType> getOutputType() {
        return outputTypes;
    }

    public void addOutputType(KeywordType type) {
        this.outputTypes.add(type);
    }

    public void addOutputTypes(Set<KeywordType> types) {
        if (types != null) {
            this.outputTypes.addAll(types);
        }
    }
}
