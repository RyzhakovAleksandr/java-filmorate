package ru.yandex.practicum.filmorate.storage.user;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.*;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@Primary
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<User> userRowMapper = (rs, rowNum) -> User.builder()
            .id(rs.getLong("user_id"))
            .email(rs.getString("email"))
            .login(rs.getString("login"))
            .name(rs.getString("full_name"))
            .birthday(rs.getDate("birthday").toLocalDate())
            .build();

    @Override
    public User create(User user) {
        String sql = "INSERT INTO users (email, login, full_name, birthday) VALUES (?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        try {
            log.info("Создаем пользователя: email={}, login={}", user.getEmail(), user.getLogin());

            jdbcTemplate.update(connection -> {
                PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"user_id"});
                stmt.setString(1, user.getEmail());
                stmt.setString(2, user.getLogin());
                stmt.setString(3, user.getName());
                stmt.setDate(4, Date.valueOf(user.getBirthday()));
                return stmt;
            }, keyHolder);

            user.setId(keyHolder.getKey().longValue());
            log.info("Пользователь успешно создан: id={}", user.getId());
            return user;

        } catch (Exception e) {
            log.error("Ошибка при создании пользователя: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public User update(User user) {
        String sql = "UPDATE users SET email = ?, login = ?, full_name = ?, birthday = ? WHERE user_id = ?";

        jdbcTemplate.update(sql,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                Date.valueOf(user.getBirthday()),
                user.getId());

        return user;
    }

    @Override
    public List<User> findAll() {
        String sql = "SELECT * FROM users";
        return jdbcTemplate.query(sql, userRowMapper);
    }

    @Override
    public Optional<User> findById(Long id) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        List<User> users = jdbcTemplate.query(sql, userRowMapper, id);
        return users.stream().findFirst();
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM users WHERE user_id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    @Transactional
    public void addFriend(Long userId, Long friendId) {
        String checkSql = "SELECT COUNT(*) FROM friends WHERE user_id = ? AND friend_id = ?";
        Integer exists = jdbcTemplate.queryForObject(checkSql, Integer.class, userId, friendId);

        if (exists == null || exists == 0) {
            String sql = "INSERT INTO friends (user_id, friend_id, status) VALUES (?, ?, TRUE)";
            jdbcTemplate.update(sql, userId, friendId);
        }
    }

    @Override
    public void removeFriend(Long userId, Long friendId) {
        String sql = "DELETE FROM friends WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(sql, userId, friendId);
    }

    @Override
    public List<User> getFriends(Long userId) {
        String sql = "SELECT u.* FROM users u " +
                "JOIN friends f ON u.user_id = f.friend_id " +
                "WHERE f.user_id = ? AND f.status = TRUE";
        return jdbcTemplate.query(sql, userRowMapper, userId);

    }

    @Override
    public List<User> getCommonFriends(Long userId, Long otherUserId) {
        String sql = "SELECT u.* FROM users u " +
                "JOIN friends f1 ON u.user_id = f1.friend_id " +
                "JOIN friends f2 ON u.user_id = f2.friend_id " +
                "WHERE f1.user_id = ? AND f2.user_id = ? AND f1.status = TRUE AND f2.status = TRUE";
        return jdbcTemplate.query(sql, userRowMapper, userId, otherUserId);
    }
}