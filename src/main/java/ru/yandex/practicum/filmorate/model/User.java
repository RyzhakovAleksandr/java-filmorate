package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Builder
public class User {
    Long id;

    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Email должен быть корректным")
    String email;

    @NotBlank(message = "Логин не может быть пустым")
    @Pattern(regexp = "\\S+", message = "Логин не может содержать пробелы")
    String login;

    String name;

    @Past(message = "Дата рождения не может быть в будущем")
    LocalDate birthday;
}
