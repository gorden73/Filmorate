package ru.yandex.practicum.filmorate.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.dao.DirectorDao;
import ru.yandex.practicum.filmorate.dao.impl.DirectorDaoImpl;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.DirectorService;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {
    private FilmController controller;
    private Film film;
    private Film film1;
    private Film film2;
    private Film film3;
    private Film film4;
    private Film film5;
    private Film film6;
    private Film film7;
    private Film film8;
    private Film film9;
    private Film film10;
    private UserService userService;

    @BeforeEach
    void start() {
        FilmStorage filmStorage = new InMemoryFilmStorage();
        DirectorDao directorDao = new DirectorDaoImpl(new JdbcTemplate());
        controller = new FilmController(new FilmService(filmStorage,
                new DirectorService(directorDao),
                new UserService(new InMemoryUserStorage())));
        createFilmsForTests();
    }

    private void createFilmsForTests() {
        film = new Film(1, "The Rock", "Starring Nicolas Cage and Sean Connery",
                LocalDate.of(1996, 6, 7), 136, new Mpa(1));
        film1 = new Film(1, "", "Starring Nicolas Cage and Sean Connery",
                LocalDate.of(1996, 6, 7), 136, new Mpa(1));
        film2 = new Film(1, "The Rock", "Disillusioned Brigadier General " +
                "Francis Hummel and his second-in-command Major Tom Baxter lead a rogue group of " +
                "U.S. Force Recon Marines against a heavily guarded naval weapons depot to steal " +
                "a stockpile of 16 VX gas-loaded M55 rockets, ultimately losing one of their own " +
                "men in the process. The next day, along with newly recruited Captains Frye and " +
                "Darrow, Hummel and his men seize control of Alcatraz Island, taking 81 tourists " +
                "hostage. ",
                LocalDate.of(1996, 6, 7), 136, new Mpa(1));
        film3 = new Film(1, "The Rock3", "Starring Nicolas Cage and Sean " +
                "Connery3", LocalDate.of(1895, 12, 27), 131,
                new Mpa(1));
        film4 = new Film(1, "The Rock4", "Starring Nicolas Cage and Sean " +
                "Connery4", LocalDate.of(1994, 6, 7), 0,
                new Mpa(1));
        film5 = new Film(1, "The Rock5", "Starring Nicolas Cage and Sean " +
                "Connery5", LocalDate.of(1996, 5, 7), -8,
                new Mpa(1));
        film6 = new Film(1, "The Rock6", "Starring Nicolas Cage and Sean " +
                "Connery6", LocalDate.of(1895, 12, 28), 132,
                new Mpa(1));
        film7 = new Film(1, "The Rock7", "Starring Nicolas Cage and Sean " +
                "Connery7", LocalDate.of(1895, 12, 29), 130,
                new Mpa(1));
        film8 = new Film(1, "The Rock12", "Disillusioned Brigadier General " +
                "Francis Hummel and his second-in-command Major Tom Baxter lead a rogue group of " +
                "U.S. Force Recon Marines against a heavily guarded naval weapons depot to steal " +
                "a stockp", LocalDate.of(1994, 6, 7), 134,
                new Mpa(1));
        film9 = new Film(1, "The Rock3", "Disillusioned Brigadier General " +
                "Francis Hummel and his second-in-command Major Tom Baxter lead a rogue group of " +
                "U.S. Force Recon Marines against a heavily guarded naval weapons depot to steal " +
                "a stock", LocalDate.of(1993, 6, 7), 137,
                new Mpa(1));
        film10 = new Film(1, "The Rock1", "Disillusioned Brigadier General " +
                "Francis Hummel and his second-in-command Major Tom Baxter lead a rogue group of " +
                "U.S. Force Recon Marines against a heavily guarded naval weapons depot to steal " +
                "a stock", LocalDate.of(1995, 6, 7), 135,
                new Mpa(1));
    }

    @Test
    void shouldReturnAllFilms() {
        assertEquals(0, controller.getAllFilms().size(), "Хранилище должно быть " +
                "пустым.");
        controller.addFilm(film);
        Collection<Film> films = controller.getAllFilms();
        assertEquals(1, films.size(), "Хранилище не должно быть пустым.");
        assertTrue(films.contains(film), "Фильм не добавлен.");
    }

    @Test
    void shouldAddFilmWhenDataIsValid() {
        controller.addFilm(film);
        assertTrue(controller.getAllFilms().contains(film), "Фильм не добавлен в " +
                "хранилище.");
    }

    @Test
    void shouldNotAddFilmWhenNameIsEmpty() {
        assertThrows(ValidationException.class, () -> controller.addFilm(film1),
                "Название фильма не пустое.");
        assertFalse(controller.getAllFilms().contains(film1), "Фильм добавлен в хранилище.");
    }

    @Test
    void shouldNotAddFilmWhenDescriptionIsOver200Symbols() {
        assertThrows(ValidationException.class, () -> controller.addFilm(film2),
                "Описание меньше 200.");
        assertFalse(controller.getAllFilms().contains(film2), "Фильм добавлен в хранилище.");
    }

    @Test
    void shouldAddFilmWhenDescriptionIsEquals200Symbols() {
        controller.addFilm(film8);
        assertTrue(controller.getAllFilms().contains(film8), "Фильм не добавлен в хранилище.");
    }

    @Test
    void shouldAddFilmWhenDescriptionIsLess200Symbols() {
        controller.addFilm(film9);
        assertTrue(controller.getAllFilms().contains(film9), "Фильм не добавлен в хранилище.");
    }

    @Test
    void shouldAddFilmWhenReleaseDateIsEqualsBirthdayOfMovie() {
        controller.addFilm(film6);
        assertTrue(controller.getAllFilms().contains(film6), "Фильм не добавлен в хранилище.");
    }

    @Test
    void shouldAddFilmWhenReleaseDateIsAfterBirthdayOfMovie() {
        controller.addFilm(film7);
        assertTrue(controller.getAllFilms().contains(film7), "Фильм не добавлен в хранилище.");
    }

    @Test
    void shouldNotAddFilmWhenReleaseDateIsBeforeBirthdayOfMovie() {
        assertThrows(ValidationException.class, () -> controller.addFilm(film3),
                "Дата выхода фильма позже 28.12.1895");
        assertFalse(controller.getAllFilms().contains(film3), "Фильм добавлен в хранилище.");
    }

    @Test
    void shouldNotAddFilmWhenDurationIsEqualsZero() {
        assertThrows(ValidationException.class, () -> controller.addFilm(film4),
                "Продолжительность фильма не равна 0.");
        assertFalse(controller.getAllFilms().contains(film4), "Фильм добавлен в хранилище.");
    }

    @Test
    void shouldNotAddFilmWhenDurationIsNegative() {
        assertThrows(ValidationException.class, () -> controller.addFilm(film5),
                "Продолжительность фильма положительная.");
        assertFalse(controller.getAllFilms().contains(film5), "Фильм добавлен в хранилище.");
    }

    @Test
    void shouldUpdateFilmWhenDataIsValid() {
        controller.addFilm(film);
        assertEquals(1, controller.getAllFilms().size(), "Хранилище не должно быть пустым.");
        assertTrue(controller.getAllFilms().contains(film), "Фильм не добавлен в хранилище.");
        controller.updateFilm(film10);
        assertEquals(1, controller.getAllFilms().size(), "Хранилище не должно быть" +
                " пустым.");
        assertEquals(film.getName(), film10.getName(), "Названия фильмов не совпадают.");
        assertEquals(film.getDescription(), film10.getDescription(), "Описания фильмов не" +
                " совпадают.");
        assertEquals(film.getReleaseDate(), film10.getReleaseDate(), "Даты выхода фильмов" +
                " не совпадают.");
        assertEquals(film.getDuration(), film10.getDuration(), "Продолжительности фильмов" +
                " не совпадают.");
    }

    @Test
    void shouldNotUpdateFilmWhenNameIsEmpty() {
        controller.addFilm(film);
        assertThrows(ValidationException.class, () -> controller.updateFilm(film1),
                "Название фильма не пустое.");
    }

    @Test
    void shouldNotUpdateFilmWhenDescriptionIsOver200Symbols() {
        controller.addFilm(film);
        assertThrows(ValidationException.class, () -> controller.updateFilm(film2),
                "Описание меньше 200 символов.");
    }

    @Test
    void shouldUpdateFilmWhenDescriptionIsEquals200Symbols() {
        controller.addFilm(film);
        assertNotEquals(film.getName(), film8.getName(), "Названия фильмов совпадают.");
        assertNotEquals(film.getDescription(), film8.getDescription(), "Описания фильмов" +
                " совпадают.");
        assertNotEquals(film.getReleaseDate(), film8.getReleaseDate(), "Даты выхода " +
                "фильмов совпадают.");
        assertNotEquals(film.getDuration(), film8.getDuration(), "Продолжительности " +
                "фильмов совпадают.");
        controller.updateFilm(film8);
        assertEquals(film.getName(), film8.getName(), "Названия фильмов не совпадают.");
        assertEquals(film.getDescription(), film8.getDescription(), "Описания фильмов " +
                "не совпадают.");
        assertEquals(film.getReleaseDate(), film8.getReleaseDate(), "Даты выхода фильмов" +
                " не совпадают.");
        assertEquals(film.getDuration(), film8.getDuration(), "Продолжительности фильмов" +
                " не совпадают.");
    }

    @Test
    void shouldUpdateFilmWhenDescriptionIsLess200Symbols() {
        controller.addFilm(film);
        assertNotEquals(film.getName(), film9.getName(), "Названия фильмов совпадают.");
        assertNotEquals(film.getDescription(), film9.getDescription(), "Описания фильмов" +
                " совпадают.");
        assertNotEquals(film.getReleaseDate(), film9.getReleaseDate(), "Даты выхода" +
                " фильмов совпадают.");
        assertNotEquals(film.getDuration(), film9.getDuration(), "Продолжительности" +
                " фильмов совпадают.");
        controller.updateFilm(film9);
        assertEquals(film.getName(), film9.getName(), "Названия фильмов не совпадают.");
        assertEquals(film.getDescription(), film9.getDescription(), "Описания фильмов не" +
                " совпадают.");
        assertEquals(film.getReleaseDate(), film9.getReleaseDate(), "Даты выхода фильмов" +
                " не совпадают.");
        assertEquals(film.getDuration(), film9.getDuration(), "Продолжительности фильмов" +
                " не совпадают.");
    }

    @Test
    void shouldUpdateFilmWhenReleaseDateIsEqualsBirthdayOfMovie() {
        controller.addFilm(film);
        assertNotEquals(film.getName(), film6.getName(), "Названия фильмов совпадают.");
        assertNotEquals(film.getDescription(), film6.getDescription(), "Описания фильмов" +
                " совпадают.");
        assertNotEquals(film.getReleaseDate(), film6.getReleaseDate(), "Даты выхода" +
                " фильмов совпадают.");
        assertNotEquals(film.getDuration(), film6.getDuration(), "Продолжительности" +
                " фильмов совпадают.");
        controller.updateFilm(film6);
        assertEquals(film.getName(), film6.getName(), "Названия фильмов не совпадают.");
        assertEquals(film.getDescription(), film6.getDescription(), "Описания фильмов не" +
                " совпадают.");
        assertEquals(film.getReleaseDate(), film6.getReleaseDate(), "Даты выхода фильмов" +
                " не совпадают.");
        assertEquals(film.getDuration(), film6.getDuration(), "Продолжительности фильмов" +
                " не совпадают.");
    }

    @Test
    void shouldUpdateFilmWhenReleaseDateIsAfterBirthdayOfMovie() {
        controller.addFilm(film);
        assertNotEquals(film.getName(), film7.getName(), "Названия фильмов совпадают.");
        assertNotEquals(film.getDescription(), film7.getDescription(), "Описания фильмов" +
                " совпадают.");
        assertNotEquals(film.getReleaseDate(), film7.getReleaseDate(), "Даты выхода " +
                "фильмов совпадают.");
        assertNotEquals(film.getDuration(), film7.getDuration(), "Продолжительности " +
                "фильмов совпадают.");
        controller.updateFilm(film7);
        assertEquals(film.getName(), film7.getName(), "Названия фильмов не совпадают.");
        assertEquals(film.getDescription(), film7.getDescription(), "Описания фильмов не" +
                " совпадают.");
        assertEquals(film.getReleaseDate(), film7.getReleaseDate(), "Даты выхода фильмов" +
                " не совпадают.");
        assertEquals(film.getDuration(), film7.getDuration(), "Продолжительности фильмов" +
                " не совпадают.");
    }

    @Test
    void shouldNotUpdateFilmWhenReleaseDateIsBeforeBirthdayOfMovie() {
        controller.addFilm(film);
        assertThrows(ValidationException.class, () -> controller.updateFilm(film3),
                "Дата выхода фильма позже 28.12.1895");
        assertNotEquals(film.getName(), film3.getName(), "Названия фильмов совпадают.");
        assertNotEquals(film.getDescription(), film3.getDescription(), "Описания фильмов" +
                " совпадают.");
        assertNotEquals(film.getReleaseDate(), film3.getReleaseDate(), "Даты выхода " +
                "фильмов совпадают.");
        assertNotEquals(film.getDuration(), film3.getDuration(), "Продолжительности " +
                "фильмов совпадают.");
    }

    @Test
    void shouldNotUpdateFilmWhenDurationIsEqualsZero() {
        controller.addFilm(film);
        assertThrows(ValidationException.class, () -> controller.updateFilm(film4),
                "Продолжительность фильма не равна 0.");
        assertNotEquals(film.getName(), film4.getName(), "Названия фильмов совпадают.");
        assertNotEquals(film.getDescription(), film4.getDescription(), "Описания фильмов " +
                "совпадают.");
        assertNotEquals(film.getReleaseDate(), film4.getReleaseDate(), "Даты выхода " +
                "фильмов совпадают.");
        assertNotEquals(film.getDuration(), film4.getDuration(), "Продолжительности " +
                "фильмов совпадают.");
    }

    @Test
    void shouldNotUpdateFilmWhenDurationIsNegative() {
        controller.addFilm(film);
        assertThrows(ValidationException.class, () -> controller.updateFilm(film5),
                "Продолжительность фильма положительная.");
        assertNotEquals(film.getName(), film5.getName(), "Названия фильмов совпадают.");
        assertNotEquals(film.getDescription(), film5.getDescription(), "Описания фильмов" +
                " совпадают.");
        assertNotEquals(film.getReleaseDate(), film5.getReleaseDate(), "Даты выхода " +
                "фильмов совпадают.");
        assertNotEquals(film.getDuration(), film5.getDuration(), "Продолжительности " +
                "фильмов совпадают.");
    }
}