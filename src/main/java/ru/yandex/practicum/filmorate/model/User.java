package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Data;

import java.time.LocalDate;

@Data
public class User {
    private Long id;
    @NotNull(message = "Email не должен быть пустым")
    @Email(message = "Ошибка ввода почты")
    private String email;
    @NotBlank(message = "Логин не должен быть пустым")
    private String login;
    private String name;
    @Past
    private LocalDate birthday;
}
