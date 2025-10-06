package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFindException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public User createUser(User user) {
        validateUser(user);
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return userStorage.create(user);
    }

    public User updateUser(User user) {
        if (user.getId() == null) {
            throw new NotFindException("ID пользователя не может быть null");
        }

        User existingUser = userStorage.findById(user.getId())
                .orElseThrow(() -> new NotFindException("Пользователь с id " + user.getId() + " не найден"));

        validateUser(user);
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return userStorage.update(user);
    }

    public List<User> getAllUsers() {
        return userStorage.findAll();
    }

    public User getUserById(Long id) {
        return userStorage.findById(id)
                .orElseThrow(() -> new RuntimeException("Пользователем с номером не найден: " + id));
    }

    public void addFriend(Long userId, Long friendId) {
        User user = userStorage.findById(userId)
                .orElseThrow(() -> new NotFindException("Пользователь с id " + userId + " не найден"));

        User friend = userStorage.findById(friendId)
                .orElseThrow(() -> new NotFindException("Пользователь с id " + friendId + " не найден"));

        userStorage.addFriend(userId, friendId);
    }

    public void removeFriend(Long userId, Long friendId) {
        User user = userStorage.findById(userId)
                .orElseThrow(() -> new NotFindException("Пользователь с id " + userId + " не найден"));

        User friend = userStorage.findById(friendId)
                .orElseThrow(() -> new NotFindException("Пользователь с id " + friendId + " не найден"));

        userStorage.removeFriend(userId, friendId);
    }

    public List<User> getFriends(Long userId) {
        User user = userStorage.findById(userId)
                .orElseThrow(() -> new NotFindException("Пользователь с id " + userId + " не найден"));

        return userStorage.getFriends(userId);
    }

    public List<User> getCommonFriends(Long userId, Long otherUserId) {
        User user = userStorage.findById(userId)
                .orElseThrow(() -> new NotFindException("Пользователь с id " + userId + " не найден"));

        User otherUser = userStorage.findById(otherUserId)
                .orElseThrow(() -> new NotFindException("Пользователь с id " + otherUserId + " не найден"));

        return userStorage.getCommonFriends(userId, otherUserId);
    }

    private void validateUser(User user) {
        if (user.getLogin().contains(" ")) {
            throw new RuntimeException("Логин не может содержать пробелы");
        }
    }

    public void delete(Long id) {
        userStorage.delete(id);
    }
}