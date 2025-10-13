package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFindException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GenreService {
    private final GenreStorage genreStorage;

    public List<Genre> getAllGenres() {
        return genreStorage.findAll();
    }

    public Genre getGenreById(Long id) {
        return genreStorage.findById(id)
                .orElseThrow(() -> new NotFindException(String.format("Жанр с id %d не найден", id)));
    }
}