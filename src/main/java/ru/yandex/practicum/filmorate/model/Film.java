package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.AssertTrue;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.Month;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class Film {
    private static final int MAX_MESSAGE_LENGTH = 200;
    private static final int CINEMA_BIRTH_YEAR = 1895;
    private static final int CINEMA_BIRTH_DAY = 28;

    Long id;
    @NotBlank(message = "Назввание фильма не должно быть пустым")
    String name;
    @NotBlank(message = "Описание фильма не должно быть пустым")
    @Size(max = MAX_MESSAGE_LENGTH, message = "Максимальное количество символов {max}")
    String description;
    @NotNull
    LocalDate releaseDate;
    @Positive
    long duration;

    @AssertTrue(message = "Фильм не мог быть снят до изобритения кинемотографа")
    private boolean isReleaseDateValid() {
        return releaseDate.isAfter(LocalDate.of(CINEMA_BIRTH_YEAR, Month.DECEMBER, CINEMA_BIRTH_DAY));
    }
}