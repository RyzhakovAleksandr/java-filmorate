package ru.yandex.practicum.filmorate.model;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {
    private static Validator validator;
    private static User user;

    @BeforeAll
    static void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @BeforeEach
    void setUpEach() {
        user = new User();
        user.setId(1L);
        user.setEmail("other@example.ru");
        user.setLogin("other");
        user.setBirthday(LocalDate.of(1990, Month.APRIL, 22));
    }

    @Test @DisplayName("Валидация успешна")
    void validateUser() {
        assertTrue(validator.validate(user).isEmpty());
    }

    @Test @DisplayName("Ошибка - email null")
    void nullEmail() {
        user.setEmail(null);
        assertFalse(validator.validate(user).isEmpty());
    }

    @Test @DisplayName("Ошибка - email пробел")
    void emptyEmail() {
        user.setEmail(" ");
        assertFalse(validator.validate(user).isEmpty());
    }

    @Test @DisplayName("Ошибка - email введен не корректно")
    void noCorrectEmail() {
        user.setEmail("это-неправильный?эмейл@");
        assertFalse(validator.validate(user).isEmpty());
    }

    @Test @DisplayName("Ошибка - логин null")
    void nullLogin() {
        user.setLogin(null);
        assertFalse(validator.validate(user).isEmpty());
    }

    @Test @DisplayName("Ошибка - login пробел")
    void emptyLogin() {
        user.setLogin(" ");
        assertFalse(validator.validate(user).isEmpty());
    }

    @Test @DisplayName("Ошибка - дата рождения из будущего")
    void futureBirthday() {
        user.setBirthday(LocalDate.now());
        assertFalse(validator.validate(user).isEmpty());
    }
}