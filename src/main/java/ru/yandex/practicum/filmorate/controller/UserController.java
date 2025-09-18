package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFindException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private static final String USER_BY_ID_PATH = "/{userId}";
    private static final String FRIEND_ACTION_PATH = "/{id}/friends/{friendId}";

    private final UserService userService;

    @GetMapping
    public Collection<User> getUsers() {
        return userService.getUsers();
    }

    @GetMapping(USER_BY_ID_PATH)
    public User getUser(@PathVariable long userId) {
        Optional<User> user = userService.getUser(userId);
        return user.orElseThrow(
                () -> new NotFindException(String.format("Пользователь с номером %d не найден", userId))
        );
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User create(@Valid @RequestBody User user) {
        return userService.create(user);
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        return userService.update(user);
    }

    @DeleteMapping(USER_BY_ID_PATH)
    public User delete(@PathVariable long userId) {
        return userService.delete(userId);
    }

    @PutMapping(FRIEND_ACTION_PATH)
    public void addFriend(@PathVariable("id") long userId, @PathVariable long friendId) {
        userService.addFriend(userId, friendId);
    }

    @DeleteMapping(FRIEND_ACTION_PATH)
    public void removeFriend(@PathVariable("id") long userId, @PathVariable long friendId) {
        userService.removeFriend(userId, friendId);
    }

    @GetMapping("/{id}/friends")
    public Collection<User> getFriends(@PathVariable long id) {
        return userService.getFriends(id);
    }

    @GetMapping("/{firstId}/friends/common/{secondId}")
    public Collection<User> getCommonFriends(@PathVariable long firstId, @PathVariable long secondId) {
        return userService.getCommonFriends(firstId, secondId);
    }
}
