package ru.yandex.practicum.filmorate.model;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FilmTest {
    private static Validator validator;
    private static Film film;

    @BeforeAll
    static void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @BeforeEach
    void setUpEach() {
        film = new Film();
        film.setId(1L);
        film.setName("Film 1");
        film.setDescription("description about Film 1");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(9999L);
    }

    @Test
    @DisplayName("Валидация успешна")
    void validateFilm() {
        assertTrue(validator.validate(film).isEmpty());
    }

    @Test
    @DisplayName("Ошибка - Название фильма пробел")
    void emptyName() {
        film.setName(" ");
        assertFalse(validator.validate(film).isEmpty());
    }

    @Test
    @DisplayName("Ошибка - Название фильма пустое")
    void nullName() {
        film.setName(null);
        assertFalse(validator.validate(film).isEmpty());
    }

    @Test
    @DisplayName("Ошибка - Описание фильма пробел")
    void emptyDescription() {
        film.setDescription(" ");
        assertFalse(validator.validate(film).isEmpty());
    }

    @Test
    @DisplayName("Ошибка - Описание фильма пустое")
    void nullDescription() {
        film.setDescription(null);
        assertFalse(validator.validate(film).isEmpty());
    }

    @Test
    @DisplayName("Описание 200 символов")
    void normalLengthDescription() {
        film.setDescription("1".repeat(200));
        assertTrue(validator.validate(film).isEmpty());
    }

    @Test
    @DisplayName("Ошибка - Описание слишком длинное")
    void overLengthDescription() {
        film.setDescription("description about Film 1".repeat(50));
        assertFalse(validator.validate(film).isEmpty());
    }

    @Test
    @DisplayName("Ошибка - Дата раньше создания кинемотографа")
    void releaseBefore1895() {
        film.setReleaseDate(LocalDate.of(1895, Month.DECEMBER, 25));
        assertFalse(validator.validate(film).isEmpty());
    }

    @Test
    @DisplayName("Ошибка - Продолжительность отрицательная")
    void negativeDuration() {
        film.setDuration(-9999L);
        assertFalse(validator.validate(film).isEmpty());
    }

    @Test
    @DisplayName("Ошибка - Продолжительность отрицательная")
    void zeroDuration() {
        film.setDuration(0);
        assertFalse(validator.validate(film).isEmpty());
    }
}