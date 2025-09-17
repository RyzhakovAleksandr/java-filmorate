package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {

    Collection<User> getUsers();

    Optional<User> getUser(long id);

    User create(User user);

    User update(User user);

    User delete(long id);

    boolean existsById(long id);

}
