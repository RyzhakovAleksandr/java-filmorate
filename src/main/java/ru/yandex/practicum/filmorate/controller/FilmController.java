package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFindException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
public class FilmController {
    private static final String LIKE_PATH = "/{id}/like/{userId}";
    private static final String FILM_ID_PATH = "/{filmId}";

    private final FilmService filmService;

    @GetMapping
    public Collection<Film> getFilms() {
        return filmService.getAllFilms();
    }

    @GetMapping(FILM_ID_PATH)
    public Film getFilm(@PathVariable long filmId) {
        try {
            return filmService.getFilmById(filmId);
        } catch (RuntimeException e) {
            throw new NotFindException(String.format("Фильм с номером id=%d, не найден", filmId));
        }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Film create(@Valid @RequestBody Film film) {
        return filmService.createFilm(film);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        return filmService.updateFilm(film);
    }

    @DeleteMapping(FILM_ID_PATH)
    public Film delete(@PathVariable long filmId) {
        Film film = getFilm(filmId);
        filmService.delete(filmId);
        return film;
    }

    @PutMapping(LIKE_PATH)
    public void addLike(@PathVariable long id, @PathVariable long userId) {
        filmService.addLike(id, userId);
    }

    @DeleteMapping(LIKE_PATH)
    public void removeLike(@PathVariable long id, @PathVariable long userId) {
        filmService.removeLike(id, userId);
    }

    @GetMapping("/popular")
    public Collection<Film> getPopularFilms(@RequestParam(defaultValue = "10") int count) {
        return filmService.getPopularFilms(count);
    }
}