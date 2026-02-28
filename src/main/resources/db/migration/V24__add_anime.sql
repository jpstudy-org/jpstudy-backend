-- Anime cache table (data from Jikan/MAL API)
CREATE TABLE anime (
    id          BIGSERIAL PRIMARY KEY,
    mal_id      INTEGER NOT NULL UNIQUE,
    title       VARCHAR(500) NOT NULL,
    title_japanese VARCHAR(500),
    image_url   VARCHAR(1000),
    synopsis    TEXT,
    type        VARCHAR(20),
    episodes    INTEGER,
    status      VARCHAR(20),
    score       DOUBLE PRECISION,
    scored_by   INTEGER,
    rank        INTEGER,
    popularity  INTEGER,
    members     INTEGER,
    year        INTEGER,
    season      VARCHAR(20),
    rating      VARCHAR(50),
    source      VARCHAR(50),
    duration    VARCHAR(50),
    aired_from  DATE,
    aired_to    DATE,
    trailer_url VARCHAR(1000),
    fetched_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_anime_mal_id ON anime (mal_id);
CREATE INDEX idx_anime_score ON anime (score DESC NULLS LAST);
CREATE INDEX idx_anime_popularity ON anime (popularity ASC NULLS LAST);
CREATE INDEX idx_anime_year ON anime (year);
CREATE INDEX idx_anime_status ON anime (status);
CREATE INDEX idx_anime_title ON anime (title);

-- Anime genres (ElementCollection)
CREATE TABLE anime_genre (
    anime_id BIGINT NOT NULL REFERENCES anime(id) ON DELETE CASCADE,
    genre    VARCHAR(100) NOT NULL
);

CREATE INDEX idx_anime_genre_anime_id ON anime_genre (anime_id);
CREATE INDEX idx_anime_genre_genre ON anime_genre (genre);

-- Curated anime lists (trending, featured, seasonal)
CREATE TABLE anime_list (
    id         BIGSERIAL PRIMARY KEY,
    anime_id   BIGINT NOT NULL REFERENCES anime(id) ON DELETE CASCADE,
    list_type  VARCHAR(20) NOT NULL,
    rank_order INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_anime_list_type ON anime_list (list_type);
CREATE UNIQUE INDEX idx_anime_list_unique ON anime_list (anime_id, list_type);
