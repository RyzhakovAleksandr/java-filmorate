package ru.yandex.practicum.filmorate.storage.genre;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Genre> genreRowMapper = (rs, rowNum) -> Genre.builder()
            .id(rs.getLong("genre_id"))
            .name(rs.getString("name"))
            .build();

    @Override
    public List<Genre> findAll() {
        String sql = "SELECT * FROM genre";
        return jdbcTemplate.query(sql, genreRowMapper);
    }

    @Override
    public Optional<Genre> findById(Long id) {
        String sql = "SELECT * FROM genre WHERE genre_id = ?";
        List<Genre> genreList = jdbcTemplate.query(sql, genreRowMapper, id);
        return genreList.stream().findFirst();
    }
}