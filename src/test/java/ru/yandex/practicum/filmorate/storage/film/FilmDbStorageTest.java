package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@Import({UserDbStorage.class, FilmDbStorage.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmDbStorageTest {

    private final UserDbStorage userStorage;
    private final FilmDbStorage filmStorage;

    @Test
    void testFindFilmById() {
        Film newFilm = Film.builder()
                .name("Test Film")
                .description("Test Description")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(120)
                .mpa(Mpa.builder().id(1L).build())
                .build();
        Film createdFilm = filmStorage.create(newFilm);

        Optional<Film> filmOptional = filmStorage.findById(createdFilm.getId());

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film)
                                .hasFieldOrPropertyWithValue("id", createdFilm.getId())
                                .hasFieldOrPropertyWithValue("name", "Test Film")
                                .hasFieldOrPropertyWithValue("description", "Test Description")
                );
    }

    @Test
    void testCreateFilm() {
        Film newFilm = Film.builder()
                .name("New Film")
                .description("New Description")
                .releaseDate(LocalDate.of(2020, 5, 5))
                .duration(150)
                .mpa(Mpa.builder().id(3L).build())
                .build();

        Film createdFilm = filmStorage.create(newFilm);

        assertThat(createdFilm).isNotNull();
        assertThat(createdFilm.getId()).isNotNull();
        assertThat(createdFilm.getName()).isEqualTo("New Film");
        assertThat(createdFilm.getMpa().getId()).isEqualTo(3L);
    }

    @Test
    void testUpdateFilm() {
        Film film = Film.builder()
                .name("Old Film")
                .description("Old Description")
                .releaseDate(LocalDate.of(2010, 1, 1))
                .duration(100)
                .mpa(Mpa.builder().id(1L).build())
                .build();
        Film createdFilm = filmStorage.create(film);

        Film updatedFilm = Film.builder()
                .id(createdFilm.getId())
                .name("Updated Film")
                .description("Updated Description")
                .releaseDate(LocalDate.of(2011, 2, 2))
                .duration(110)
                .mpa(Mpa.builder().id(2L).build())
                .build();
        filmStorage.update(updatedFilm);

        Optional<Film> foundFilm = filmStorage.findById(createdFilm.getId());
        assertThat(foundFilm)
                .isPresent()
                .hasValueSatisfying(f ->
                        assertThat(f)
                                .hasFieldOrPropertyWithValue("name", "Updated Film")
                                .hasFieldOrPropertyWithValue("description", "Updated Description")
                                .hasFieldOrPropertyWithValue("duration", 110)
                );
    }

    @Test
    void testFindAllFilms() {
        Film film1 = Film.builder()
                .name("Film One")
                .description("Description One")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(100)
                .mpa(Mpa.builder().id(1L).build())
                .build();
        Film film2 = Film.builder()
                .name("Film Two")
                .description("Description Two")
                .releaseDate(LocalDate.of(2001, 2, 2))
                .duration(120)
                .mpa(Mpa.builder().id(2L).build())
                .build();
        filmStorage.create(film1);
        filmStorage.create(film2);

        List<Film> films = filmStorage.findAll();

        assertThat(films).hasSize(2);
        assertThat(films)
                .extracting(Film::getName)
                .contains("Film One", "Film Two");
    }

    @Test
    void testAddLikeAndGetPopular() {
        User user = User.builder()
                .email("user@mail.ru")
                .login("userLogin")
                .name("Test User")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        Film film = Film.builder()
                .name("Popular Film")
                .description("Popular Description")
                .releaseDate(LocalDate.of(2020, 1, 1))
                .duration(130)
                .mpa(Mpa.builder().id(1L).build())
                .build();

        User createdUser = userStorage.create(user);
        Film createdFilm = filmStorage.create(film);

        filmStorage.addLike(createdFilm.getId(), createdUser.getId());
        List<Film> popularFilms = filmStorage.getPopularFilms(10);

        assertThat(popularFilms).isNotEmpty();
        assertThat(popularFilms.get(0).getId()).isEqualTo(createdFilm.getId());
    }
}