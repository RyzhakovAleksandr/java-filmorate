package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.*;
import java.sql.Date;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Repository
@Primary
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Film> filmRowMapper = (rs, rowNum) -> {
        Film film = Film.builder()
                .id(rs.getLong("film_id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getInt("duration"))
                .mpa(Mpa.builder()
                        .id(rs.getLong("mpa_id"))
                        .name(rs.getString("mpa_name"))
                        .build())
                .build();

        film.setGenres(loadFilmGenres(film.getId()));
        return film;
    };

    @Override
    public Film create(Film film) {
        String sql = "INSERT INTO film (name, description, release_date, duration, mpa_id) VALUES (?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"film_id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setInt(4, film.getDuration());
            stmt.setLong(5, film.getMpa().getId());
            return stmt;
        }, keyHolder);

        film.setId(keyHolder.getKey().longValue());

        log.info("Создаем фильм ID: {}, название: {}", film.getId(), film.getName());
        log.info("Жанры для сохранения: {}", film.getGenres());

        saveFilmGenres(film);

        return film;
    }

    @Override
    public Film update(Film film) {
        String sql = "UPDATE film SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? WHERE film_id = ?";

        jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());

        updateFilmGenres(film);

        return film;
    }

    @Override
    public List<Film> findAll() {
        String sql = "SELECT f.*, m.name as mpa_name FROM film f " +
                "JOIN mpa_rating m ON f.mpa_id = m.mpa_id " +
                "ORDER BY f.film_id"; // Добавлена сортировка по ID
        List<Film> films = jdbcTemplate.query(sql, filmRowMapper);
        return films;
    }

    @Override
    public Optional<Film> findById(Long id) {
        String sql = "SELECT f.*, m.name as mpa_name FROM film f " +
                "JOIN mpa_rating m ON f.mpa_id = m.mpa_id " +
                "WHERE f.film_id = ?";
        List<Film> films = jdbcTemplate.query(sql, filmRowMapper, id);
        return films.stream().findFirst();
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM film WHERE film_id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        String sql = "INSERT INTO likes (film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        String sql = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        String sql = "SELECT f.*, m.name as mpa_name, COUNT(l.user_id) as likes_count " +
                "FROM film f " +
                "JOIN mpa_rating m ON f.mpa_id = m.mpa_id " +
                "LEFT JOIN likes l ON f.film_id = l.film_id " +
                "GROUP BY f.film_id, m.name " +
                "ORDER BY likes_count DESC, f.film_id " + // Добавлена сортировка по ID
                "LIMIT ?";
        List<Film> films = jdbcTemplate.query(sql, filmRowMapper, count);
        return films;
    }

    private void saveFilmGenres(Film film) {
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            Set<Long> uniqueGenreIds = film.getGenres().stream()
                    .map(Genre::getId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            String sql = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";

            log.info("Сохраняем {} уникальных жанров для фильма {}", uniqueGenreIds.size(), film.getId());

            uniqueGenreIds.forEach(genreId -> {
                log.info("Добавляем жанр ID: {} для фильма ID: {}", genreId, film.getId());
                jdbcTemplate.update(sql, film.getId(), genreId);
            });
        } else {
            log.warn("Нет жанров для сохранения для фильма ID: {}", film.getId());
        }
    }

    private void updateFilmGenres(Film film) {
        String deleteSql = "DELETE FROM film_genre WHERE film_id = ?";
        jdbcTemplate.update(deleteSql, film.getId());

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            String sql = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";
            Set<Long> addedGenres = new HashSet<>();
            film.getGenres().forEach(genre -> {
                if (addedGenres.add(genre.getId())) {
                    jdbcTemplate.update(sql, film.getId(), genre.getId());
                }
            });
        }
    }

    private List<Genre> loadFilmGenres(Long filmId) {
        String sql = "SELECT g.genre_id, g.name FROM genre g " +
                "JOIN film_genre fg ON g.genre_id = fg.genre_id " +
                "WHERE fg.film_id = ? " +
                "ORDER BY g.genre_id";

        return jdbcTemplate.query(sql, (rs, rowNum) -> Genre.builder()
                .id(rs.getLong("genre_id"))
                .name(rs.getString("name"))
                .build(), filmId);
    }
}