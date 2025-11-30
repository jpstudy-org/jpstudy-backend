package orinnetwork.jpstudy.domain.filtering;

import lombok.Getter;

@Getter
public enum KeywordType {
    PROFANITY(1),   // 비속어
    INSULT(50),     // 인격 모독
    POLITICAL(99);  // 정치 발언

    private final int weight;

    KeywordType(int weight) {
        this.weight = weight;
    }

    public static KeywordType fromString(String text) {
        if (text == null) {
            return null;
        }
        for (KeywordType type : KeywordType.values()) {
            if (type.name().equalsIgnoreCase(text)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No constant with text " + text + " found");
    }
}
