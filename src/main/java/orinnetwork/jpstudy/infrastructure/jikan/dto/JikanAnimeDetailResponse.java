package orinnetwork.jpstudy.infrastructure.jikan.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record JikanAnimeDetailResponse(
        @JsonProperty("data") JikanAnimeData data
) {}
