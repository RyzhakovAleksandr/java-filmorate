package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public Collection<User> getUsers() {
        log.trace("getUsers(). Получен список пользователей");
        return users.values();
    }

    @Override
    public Optional<User> getUser(long id) {
        log.trace("getById(). Получен пользователь с индификационным номером = {}", id);
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public User create(User user) {
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.trace("create(user). Запись пользователь: {}, успешно добавлена", user);
        return user;
    }

    @Override
    public User update(User user) {
        log.trace("update(film). Фильм с индификационным номером = {} обновлен", user.getId());
        return users.put(user.getId(), user);
    }

    @Override
    public User delete(long id) {
        log.trace("deleteById(id). Пользователь с индификационным номером = {} удален", id);
        return users.remove(id);
    }

    @Override
    public boolean existsById(long id) {
        log.trace("existsById(id). Наличие пользователя с индификационным номером = {} ", id);
        return users.containsKey(id);
    }

    private long getNextId() {
        long currentId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0L);
        return ++currentId;
    }
}
