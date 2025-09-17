package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class User {
    Long id;
    @NotNull(message = "Email не должен быть пустым")
    @Email(message = "Ошибка ввода почты")
    String email;
    @NotBlank(message = "Логин не должен быть пустым")
    String login;
    String name;
    @Past(message = "День рождение не должно быть из будущего")
    LocalDate birthday;
    Set<Long> friends = new HashSet<>();

    public void addFriend(Long friendId) {
        friends.add(friendId);
    }

    public void removeFriend(Long friendId) {
        friends.remove(friendId);
    }
}
