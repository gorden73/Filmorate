package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class FilmDbStorageTest {
    private final FilmDbStorage filmDbStorage;
    private Film film;

    @BeforeEach
    public void createFilmsForTests() {
        film = new Film("The Rock1", "Starring Nicolas Cage and Sean Connery1",
                LocalDate.of(1995, 6, 7), 137, new Mpa(2));
    }

    @Test
    void shouldReturnAllFilms() {
        Map<Integer, Film> films = filmDbStorage.allFilms();
        assertThat(films).hasSize(1);
        Film returnedFilm = films.get(1);
        assertThat(returnedFilm.getId()).isEqualTo(1);
        assertThat(returnedFilm.getName()).isEqualTo("The Rock");
        assertThat(returnedFilm.getDescription()).isEqualTo("Starring Nicolas Cage and Sean Connery");
        assertThat(returnedFilm.getReleaseDate()).isEqualTo("1996-06-07");
        assertThat(returnedFilm.getDuration()).isEqualTo(136);
        assertThat(returnedFilm.getMpa().getId()).isEqualTo(1);
    }

    @Test
    void shouldAddFilm() {
        filmDbStorage.add(film);
        Map<Integer, Film> films = filmDbStorage.allFilms();
        assertThat(films).hasSize(2);
        Film returnedFilm = films.get(2);
        assertThat(returnedFilm.getId()).isEqualTo(2);
        assertThat(returnedFilm.getName()).isEqualTo(film.getName());
        assertThat(returnedFilm.getDescription()).isEqualTo(film.getDescription());
        assertThat(returnedFilm.getReleaseDate()).isEqualTo(film.getReleaseDate());
        assertThat(returnedFilm.getDuration()).isEqualTo(film.getDuration());
        assertThat(returnedFilm.getMpa().getId()).isEqualTo(film.getMpa().getId());
    }

    @Test
    void shouldUpdateFilm() {
        Film updateFilm = new Film(1, "updateName", "updateDescription", LocalDate.of(2000, 12, 12),
                100, new Mpa(4), new HashSet<>(), new ArrayList<>());
        filmDbStorage.update(updateFilm);
        assertThat(updateFilm).isEqualTo(filmDbStorage.allFilms().get(1));
    }

    @Test
    void shouldRemoveFilm() {
        assertThat(filmDbStorage.allFilms().get(1).getId()).isEqualTo(1);
        assertThat(filmDbStorage.allFilms()).hasSize(1);
        filmDbStorage.remove(1);
        assertThat(filmDbStorage.allFilms()).isEmpty();
    }
}