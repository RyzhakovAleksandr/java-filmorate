package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

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
    @Past
    LocalDate birthday;
}
