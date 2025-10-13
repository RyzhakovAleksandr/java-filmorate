package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class MpaDbStorage implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Mpa> mpaRowMapper = (rs, rowNum) -> Mpa.builder()
            .id(rs.getLong("mpa_id"))
            .name(rs.getString("name"))
            .build();

    @Override
    public List<Mpa> findAll() {
        String sql = "SELECT * FROM mpa_rating";
        return jdbcTemplate.query(sql, mpaRowMapper);
    }

    @Override
    public Optional<Mpa> findById(Long id) {
        try {
            String sql = "SELECT * FROM mpa_rating WHERE mpa_id = ?";
            List<Mpa> mpaList = jdbcTemplate.query(sql, mpaRowMapper, id);
            log.info("Найдено MPA записей для id {}: {}", id, mpaList.size());
            return mpaList.stream().findFirst();
        } catch (Exception e) {
            log.error("Ошибка при поиске MPA с id {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }
}