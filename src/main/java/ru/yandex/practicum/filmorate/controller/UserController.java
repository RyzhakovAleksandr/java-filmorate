package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFindException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> getUsers() {
        log.debug("Получена коллекция пользователей getUsers()");
        return users.values();
    }

    @PostMapping
    public User add(@Valid @RequestBody User user) {
        user.setId(getNextId());
        checkName(user);
        users.put(user.getId(), user);
        log.debug("Пользователь создан {}", user);
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        if (!users.containsKey(user.getId())) {
            log.debug("Пользователь с id {} не найден", user.getId());
            throw new NotFindException(String.format("Пользователь с id %d не найден", user.getId()));
        }
        checkName(user);
        users.put(user.getId(), user);
        log.debug("Пользователь с id {} обнавлен", user.getId());
        return user;
    }

    private long getNextId() {
        long currentId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0L);
        return ++currentId;
    }

    private void checkName(User user) {
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
    }
}
