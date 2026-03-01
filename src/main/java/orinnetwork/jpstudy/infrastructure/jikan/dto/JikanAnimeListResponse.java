package orinnetwork.jpstudy.infrastructure.jikan.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record JikanAnimeListResponse(
        @JsonProperty("data") List<JikanAnimeData> data,
        @JsonProperty("pagination") Pagination pagination
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Pagination(
            @JsonProperty("last_visible_page") Integer lastVisiblePage,
            @JsonProperty("has_next_page") Boolean hasNextPage
    ) {}
}
