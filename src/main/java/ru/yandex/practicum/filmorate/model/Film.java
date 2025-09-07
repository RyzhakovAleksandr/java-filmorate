package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.Month;

/**
 * Film.
 */
@Data
public class Film {
    private static final int MAX_MESSAGE_LENGTH = 200;

    private Long id;
    @NotBlank(message = "Назввание фильма не должно быть пустым")
    private String name;
    @NotBlank(message = "Описание фильма не должно быть пустым")
    @Size(max = MAX_MESSAGE_LENGTH, message = "Максимальное количество символов {max}")
    private String description;
    @NotNull
    private LocalDate releaseDate;
    @Positive
    private long duration;

    @AssertTrue(message = "Фильм не мог быть снят до изобритения кинемотографа")
    private boolean isReleaseDateValid() {
        return releaseDate.isAfter(LocalDate.of(1895, Month.DECEMBER, 28));
    }

    ;
}