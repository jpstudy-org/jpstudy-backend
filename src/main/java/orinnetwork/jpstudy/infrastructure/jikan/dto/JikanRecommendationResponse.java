package orinnetwork.jpstudy.infrastructure.jikan.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record JikanRecommendationResponse(
        @JsonProperty("data") List<RecommendationEntry> data
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record RecommendationEntry(
            @JsonProperty("entry") JikanAnimeData entry
    ) {}
}
