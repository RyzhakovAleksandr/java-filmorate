package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;


@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Builder
public class Film {
    private static final int MAX_MESSAGE_LENGTH = 200;
    private static final int CINEMA_BIRTH_YEAR = 1895;
    private static final int CINEMA_BIRTH_DAY = 28;

    Long id;

    @NotBlank(message = "Название фильма не может быть пустым")
    String name;

    @NotBlank(message = "Описание фильма не может быть пустым")
    @Size(max = 200, message = "Описание фильма не может превышать 200 символов")
    String description;

    @NotNull(message = "Дата релиза обязательна")
    LocalDate releaseDate;

    @Positive(message = "Продолжительность фильма должна быть положительной")
    Integer duration;

    @NotNull(message = "Рейтинг MPA обязателен")
    Mpa mpa;

    List<Genre> genres;

    @AssertTrue(message = "Фильм не мог быть снят до изобритения кинемотографа")
    boolean isReleaseDateValid() {
        return releaseDate.isAfter(LocalDate.of(CINEMA_BIRTH_YEAR, Month.DECEMBER, CINEMA_BIRTH_DAY));
    }
}