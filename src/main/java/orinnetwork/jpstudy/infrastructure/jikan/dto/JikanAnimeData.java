package orinnetwork.jpstudy.infrastructure.jikan.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record JikanAnimeData(
        @JsonProperty("mal_id") Integer malId,
        @JsonProperty("title") String title,
        @JsonProperty("title_japanese") String titleJapanese,
        @JsonProperty("images") Images images,
        @JsonProperty("trailer") Trailer trailer,
        @JsonProperty("type") String type,
        @JsonProperty("episodes") Integer episodes,
        @JsonProperty("status") String status,
        @JsonProperty("score") Double score,
        @JsonProperty("scored_by") Integer scoredBy,
        @JsonProperty("rank") Integer rank,
        @JsonProperty("popularity") Integer popularity,
        @JsonProperty("members") Integer members,
        @JsonProperty("synopsis") String synopsis,
        @JsonProperty("year") Integer year,
        @JsonProperty("season") String season,
        @JsonProperty("aired") Aired aired,
        @JsonProperty("rating") String rating,
        @JsonProperty("source") String source,
        @JsonProperty("duration") String duration,
        @JsonProperty("genres") List<Genre> genres
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Images(
            @JsonProperty("jpg") Jpg jpg
    ) {
        @JsonIgnoreProperties(ignoreUnknown = true)
        public record Jpg(
                @JsonProperty("large_image_url") String largeImageUrl,
                @JsonProperty("image_url") String imageUrl
        ) {}
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Trailer(
            @JsonProperty("url") String url
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Aired(
            @JsonProperty("from") String from,
            @JsonProperty("to") String to
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Genre(
            @JsonProperty("mal_id") Integer malId,
            @JsonProperty("name") String name
    ) {}

    public String getImageUrl() {
        if (images != null && images.jpg() != null) {
            return images.jpg().largeImageUrl() != null
                    ? images.jpg().largeImageUrl()
                    : images.jpg().imageUrl();
        }
        return null;
    }

    public String getTrailerUrl() {
        return trailer != null ? trailer.url() : null;
    }

    public List<String> getGenreNames() {
        return genres != null
                ? genres.stream().map(Genre::name).toList()
                : List.of();
    }
}
