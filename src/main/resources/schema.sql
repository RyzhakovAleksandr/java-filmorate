CREATE TABLE IF NOT EXISTS genre
(
    genre_id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name     VARCHAR(64) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS mpa_rating
(
    mpa_id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name   VARCHAR(10) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS film
(
    film_id      INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name         VARCHAR(125) NOT NULL,
    mpa_id       INTEGER      NOT NULL,
    description  VARCHAR(200) NOT NULL,
    release_date DATE         NOT NULL CHECK (release_date >= '1895-12-28'),
    duration     INTEGER      NOT NULL CHECK (duration > 0),
    FOREIGN KEY (mpa_id) REFERENCES mpa_rating (mpa_id)
);

CREATE TABLE IF NOT EXISTS film_genre
(
    film_id  INTEGER NOT NULL,
    genre_id INTEGER NOT NULL,
    PRIMARY KEY (film_id, genre_id),
    FOREIGN KEY (film_id) REFERENCES film (film_id) ON DELETE CASCADE,
    FOREIGN KEY (genre_id) REFERENCES genre (genre_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS users
(
    user_id   INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    login     VARCHAR(30)  NOT NULL UNIQUE,
    email     VARCHAR(30)  NOT NULL UNIQUE,
    birthday  DATE         NOT NULL CHECK (birthday <= CURRENT_DATE())
);

CREATE TABLE IF NOT EXISTS likes
(
    film_id INTEGER NOT NULL,
    user_id INTEGER NOT NULL,
    PRIMARY KEY (film_id, user_id),
    FOREIGN KEY (film_id) REFERENCES film (film_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS friends
(
    id        INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id   INTEGER NOT NULL,
    friend_id INTEGER NOT NULL,
    status    BOOLEAN DEFAULT FALSE,
    UNIQUE (user_id, friend_id),
    FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE,
    FOREIGN KEY (friend_id) REFERENCES users (user_id) ON DELETE CASCADE,
    CHECK (user_id <> friend_id)
);

CREATE INDEX IF NOT EXISTS idx_film_mpa ON film (mpa_id);
CREATE INDEX IF NOT EXISTS idx_film_genre_film ON film_genre (film_id);
CREATE INDEX IF NOT EXISTS idx_film_genre_genre ON film_genre (genre_id);
CREATE INDEX IF NOT EXISTS idx_likes_film ON likes (film_id);
CREATE INDEX IF NOT EXISTS idx_likes_user ON likes (user_id);
CREATE INDEX IF NOT EXISTS idx_friends_user ON friends (user_id);
CREATE INDEX IF NOT EXISTS idx_friends_friend ON friends (friend_id);