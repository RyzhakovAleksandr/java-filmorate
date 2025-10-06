package ru.yandex.practicum.filmorate.storage;


import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@Import({MpaDbStorage.class, GenreDbStorage.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class MpaGenreDbStorageTest {

    private final MpaDbStorage mpaStorage;
    private final GenreDbStorage genreStorage;

    @Test
    void testFindAllMpa() {
        // When
        List<Mpa> mpaList = mpaStorage.findAll();

        // Then - проверяем, что все MPA рейтинги загружены из data.sql
        assertThat(mpaList).hasSize(5);
        assertThat(mpaList)
                .extracting(Mpa::getName)
                .contains("G", "PG", "PG-13", "R", "NC-17");
    }

    @Test
    void testFindMpaById() {
        // When
        Optional<Mpa> mpa = mpaStorage.findById(1L);

        // Then
        assertThat(mpa)
                .isPresent()
                .hasValueSatisfying(m ->
                        assertThat(m)
                                .hasFieldOrPropertyWithValue("id", 1L)
                                .hasFieldOrPropertyWithValue("name", "G")
                );
    }

    @Test
    void testFindAllGenres() {
        // When
        List<Genre> genres = genreStorage.findAll();

        // Then - проверяем, что все жанры загружены из data.sql
        assertThat(genres).hasSize(6);
        assertThat(genres)
                .extracting(Genre::getName)
                .contains("Комедия", "Драма", "Мультфильм", "Триллер", "Документальный", "Боевик");
    }

    @Test
    void testFindGenreById() {
        // When
        Optional<Genre> genre = genreStorage.findById(1L);

        // Then
        assertThat(genre)
                .isPresent()
                .hasValueSatisfying(g ->
                        assertThat(g)
                                .hasFieldOrPropertyWithValue("id", 1L)
                                .hasFieldOrPropertyWithValue("name", "Комедия")
                );
    }
}