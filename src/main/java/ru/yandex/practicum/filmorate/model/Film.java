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

    private Long id;

    @NotBlank(message = "Название фильма не может быть пустым")
    private String name;

    @NotBlank(message = "Описание фильма не может быть пустым")
    @Size(max = 200, message = "Описание фильма не может превышать 200 символов")
    private String description;

    @NotNull(message = "Дата релиза обязательна")
    private LocalDate releaseDate;

    @Positive(message = "Продолжительность фильма должна быть положительной")
    private Integer duration;

    @NotNull(message = "Рейтинг MPA обязателен")
    private Mpa mpa;

    private List<Genre> genres;

    @AssertTrue(message = "Фильм не мог быть снят до изобритения кинемотографа")
    private boolean isReleaseDateValid() {
        return releaseDate.isAfter(LocalDate.of(CINEMA_BIRTH_YEAR, Month.DECEMBER, CINEMA_BIRTH_DAY));
    }
}