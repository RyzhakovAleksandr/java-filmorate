package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public Collection<Film> getFilms() {
        log.trace("getFilms(). Получена коллекция фильмов");
        return films.values();
    }

    @Override
    public Optional<Film> getFilm(long id) {
        log.trace("getById(id). Получена информация о фильме с индификационным номером = {}", id);
        return Optional.ofNullable(films.get(id));
    }

    @Override
    public Film create(Film film) {
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.trace("create(film). Запись фильм: {}, успешно добавлена", film);
        return film;
    }

    @Override
    public Film update(Film film) {
        log.trace("update(film). Фильм с индификационным номером = {} обновлен", film.getId());
        return films.put(film.getId(), film);
    }

    @Override
    public Film delete(long id) {
        log.trace("deleteById(id). Фильм с индификационным номером = {} удален", id);
        return films.remove(id);
    }

    @Override
    public boolean existsById(long id) {
        log.trace("existsById(id). Наличие фильма с индификационным номером = {} ", id);
        return films.containsKey(id);
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0L);
        return ++currentMaxId;
    }
}
