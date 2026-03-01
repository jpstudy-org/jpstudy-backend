package orinnetwork.jpstudy.domain.anime;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AnimeStatus {
    AIRING("Currently Airing"),
    FINISHED("Finished Airing"),
    UPCOMING("Not yet aired");

    private final String jikanValue;

    public static AnimeStatus fromJikan(String jikanStatus) {
        if (jikanStatus == null) {
            return null;
        }
        for (AnimeStatus status : values()) {
            if (status.jikanValue.equalsIgnoreCase(jikanStatus)) {
                return status;
            }
        }
        return null;
    }
}
