package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFindException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public Collection<User> getUsers() {
        return userStorage.getUsers();
    }

    public Optional<User> getUser(long id) {
        return userStorage.getUser(id);
    }

    public User create(User user) {
        checkName(user);
        return userStorage.create(user);
    }

    public User update(User newUser) {
        User oldUser = getUserOrThrow(newUser.getId());

        if (newUser.getName() != null) {
            oldUser.setName(newUser.getName());
        }
        if (newUser.getLogin() != null) {
            oldUser.setLogin(newUser.getLogin());
        }
        if (newUser.getEmail() != null) {
            oldUser.setEmail(newUser.getEmail());
        }
        if (newUser.getBirthday() != null) {
            oldUser.setBirthday(newUser.getBirthday());
        }
        return userStorage.update(oldUser);
    }

    public User delete(long id) {
        return userStorage.delete(id);
    }

    public void addFriend(long userId, long friendId) {
        User user = getUserOrThrow(userId);
        User friend = getUserOrThrow(friendId);
        validateNotSelf(userId, friendId);
        log.trace("addFriend(userId, friendId). Пользователи id={} и id={}, добавились в друзья", userId, friendId);
        user.addFriend(friendId);
        friend.addFriend(userId);
    }

    public void removeFriend(long userId, long friendId) {
        User user = getUserOrThrow(userId);
        User friend = getUserOrThrow(friendId);
        validateNotSelf(userId, friendId);
        log.trace("removeFriend(userId, friendId). Пользователи id={} и id={}, больше не друзья", userId, friendId);
        user.removeFriend(friendId);
        friend.removeFriend(userId);
    }

    public Collection<User> getFriends(long userId) {
        User user = getUserOrThrow(userId);
        Set<Long> friendIds = user.getFriends();
        log.trace("getFriends(userId). Получен список друзей у пользователя с id={}", userId);
        return getListUsers(friendIds);
    }

    public Collection<User> getCommonFriends(long firstUserId, long secondUserId) {
        User firstUser = getUserOrThrow(firstUserId);
        User secondUser = getUserOrThrow(secondUserId);
        Collection<User> firstUserFriends = getListUsers(firstUser.getFriends());
        Collection<User> secondUserFriends = getListUsers(secondUser.getFriends());

        log.trace("getFriends(userId). Получены списки общих друзей у пользователей с id={} и id={}",
                firstUserId, secondUserId);

        return firstUserFriends.stream()
                .filter(secondUserFriends::contains)
                .toList();
    }

    private User getUserOrThrow(long userId) {
        return userStorage.getUser(userId)
                .orElseThrow(() -> new NotFindException(
                        String.format("Пользователь с id=%d не найден", userId)
                ));
    }

    private void validateNotSelf(long userId, long friendId) {
        if (userId == friendId) {
            throw new NotFindException("Нельзя добавлять/удалять самого себя в друзья");
        }
    }

    private void checkName(User user) {
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
    }

    private Collection<User> getListUsers(Set<Long> ids) {
        return ids.stream()
                .map(this::getUserOrThrow)
                .toList();
    }
}
