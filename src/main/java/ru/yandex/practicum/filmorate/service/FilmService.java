package ru.yandex.practicum.filmorate.service;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFindException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public Collection<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public Optional<Film> getFilm(long id) {
        return filmStorage.getFilm(id);
    }

    public Film create(Film film) {
        return filmStorage.create(film);
    }

    public Film update(Film newFilm) {
        Film oldFilm = getFilmOrThrow(newFilm.getId());

        if (newFilm.getName() != null) {
            oldFilm.setName(newFilm.getName());
        }
        if (newFilm.getDescription() != null && !newFilm.getDescription().isBlank()) {
            oldFilm.setDescription(newFilm.getDescription());
        }
        if (newFilm.getReleaseDate() != null) {
            oldFilm.setReleaseDate(newFilm.getReleaseDate());
        }
        if (newFilm.getDuration() >= 0) {
            oldFilm.setDuration(newFilm.getDuration());
        }

        return filmStorage.update(oldFilm);
    }

    public Film delete(long id) {
        return filmStorage.delete(id);
    }

    public void addLike(long filmId, long userId) {
        Film film = getFilmOrThrow(filmId);
        validateUserExists(userId);

        log.trace("Пользователь с id={} добавил like фильму с id={}", userId, filmId);
        film.addUserLike(userId);
    }

    public void removeLike(long filmId, long userId) {
        Film film = getFilmOrThrow(filmId);
        validateUserExists(userId);
        log.trace("Пользователь с id={} убрал like фильму с id={}", userId, filmId);
        film.removeUserLike(userId);
    }

    public Collection<Film> findFilmsWithTopLikes(
            @Positive(message = "Параметр count должен быть положительным числом") int count) {

        log.trace("Список фильмов по поплуярности в количестве: {}", count);

        return filmStorage.getFilms()
                .stream()
                .sorted(Comparator.comparing(Film::getLikesCount).reversed())
                .limit(count)
                .toList();
    }

    private Film getFilmOrThrow(long filmId) {
        return filmStorage.getFilm(filmId)
                .orElseThrow(() -> new NotFindException(
                        String.format("Фильм с id=%d не найден", filmId)
                ));
    }

    private void validateUserExists(long userId) {
        if (!userStorage.existsById(userId)) {
            throw new NotFindException(
                    String.format("Пользователь с id=%d не найден", userId)
            );
        }
    }
}
