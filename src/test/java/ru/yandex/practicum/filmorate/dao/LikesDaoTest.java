package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.dao.impl.FilmDbStorage;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class LikesDaoTest {
    private final LikesDao likesDao;
    private final FilmDbStorage filmDbStorage;

    @Test
    void shouldAddLike() {
        assertThat(filmDbStorage.getAllFilms().get(1).getLikes()).isEmpty();
        likesDao.addLike(1, 1);
        assertThat(filmDbStorage.getAllFilms().get(1).getLikes()).contains(1);
    }

    @Test
    void shouldRemoveLike() {
        assertThat(filmDbStorage.getAllFilms().get(1).getLikes()).isEmpty();
        likesDao.addLike(1, 1);
        assertThat(filmDbStorage.getAllFilms().get(1).getLikes()).contains(1);
        likesDao.removeLike(1, 1);
        assertThat(filmDbStorage.getAllFilms().get(1).getLikes()).isEmpty();
    }

    @Test
    void shouldReturnPopularFilms() {
        Film film = new Film(2,"The Rock1", "Starring Nicolas Cage and Sean Connery1",
                LocalDate.of(1995, 6, 7), 137, new Mpa(2), new HashSet<>());
        filmDbStorage.addFilm(film);
        likesDao.addLike(2, 1);
        assertThat(filmDbStorage.getPopularFilms()).isEqualTo(List.of(new Film(2,"The Rock1",
                "Starring Nicolas Cage and Sean Connery1", LocalDate.of(1995, 6, 7),
                137, new Mpa(2), new HashSet<>(List.of(1)), null, new HashSet<>()), new Film(1,"The Rock",
                "Starring Nicolas Cage and Sean Connery", LocalDate.of(1996, 6, 7),
                136, new Mpa(1), new HashSet<>(), null, new HashSet<>())));
    }
}