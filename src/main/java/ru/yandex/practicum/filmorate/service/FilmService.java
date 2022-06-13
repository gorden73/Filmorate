package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ElementNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final DirectorService directorService;
    private static final LocalDate MOVIE_BIRTHDAY = LocalDate.of(1895, 12, 28);

    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage, DirectorService directorService) {
        this.filmStorage = filmStorage;
        this.directorService = directorService;
    }

    private boolean checkValidData(Film film) {
        if (film.getDescription().length() > 200) {
            log.error("Описание больше 200 символов.", InMemoryFilmStorage.class);
            throw new ValidationException("Описание не должно составлять больше 200 символов.");
        }
        if (film.getDescription().isBlank() || film.getDescription().isEmpty()) {
            log.error("Описание пустое или состоит из пробелов.");
            throw new ValidationException("Описание не должно быть пустым или состоять из пробелов.");
        }
        if (film.getReleaseDate().isBefore(MOVIE_BIRTHDAY)) {
            log.error("Дата выхода фильма в прокат не может быть раньше 28.12.1895.", InMemoryFilmStorage.class);
            throw new ValidationException("Дата выхода фильма в прокат не может быть раньше 28.12.1895.");
        }
        if (film.getDuration() <= 0) {
            log.error("Продолжительность фильма отрицательное число или равно нулю.", InMemoryFilmStorage.class);
            throw new ValidationException("Продолжительность фильма должна быть больше нуля.");
        }
        return true;
    }

    private boolean checkAddValidData(Film film) {
        if (checkValidData(film)) {
            if (film.getName().isBlank()) {
                log.error("Название пустое.", InMemoryFilmStorage.class);
                throw new ValidationException("Название не может быть пустым.");
            }
        }
        return true;
    }

    private boolean checkUpdateValidData(Film film) {
        if (checkValidData(film)) {
            if (film.getId() == null) {
                log.error("Не введен id.", InMemoryFilmStorage.class);
                throw new ValidationException("Нужно задать id.");
            }
            if (!getAllFilms().containsKey(film.getId())) {
                log.error("Неверный id.", InMemoryFilmStorage.class);
                throw new ElementNotFoundException("фильм с id" + film.getId());
            }
        }
        return true;
    }

    public Map<Integer, Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film addFilm(Film film) {
        if (checkAddValidData(film)) {
            final Integer directorId = film.getDirector().stream().findAny().get().getId();
            final Optional<Director> optionalDirector = directorService.findDirectorById(directorId);
            final Film createdFilm = filmStorage.addFilm(film);
            optionalDirector.ifPresent(director ->
                    directorService.addDirector(director.getId(), createdFilm.getId()));
            return createdFilm;
        }
        return film;
    }

    public Film updateFilm(Film film) {
        if (checkUpdateValidData(film) && checkAddValidData(film)) {
            return filmStorage.updateFilm(film);
        }

        return film;
    }

    public Integer removeFilm(Integer id) {
        return filmStorage.removeFilm(id);
    }

    public Integer addLike(Integer filmId, Integer userId) {
        Map<Integer, Film> films = filmStorage.getAllFilms();
        if (!films.containsKey(filmId)) {
            throw new ElementNotFoundException("фильм " + filmId);
        }
        return filmStorage.addLike(filmId, userId);
    }

    public Integer removeLike(Integer filmId, Integer userId) {
        Map<Integer, Film> films = filmStorage.getAllFilms();
        if (!films.containsKey(filmId)) {
            throw new ElementNotFoundException("фильм " + filmId);
        }
        if (!getFilm(filmId).getLikes().contains(userId)) {
            throw new ElementNotFoundException("лайк пользователя " + userId);
        }
        films.get(filmId).getLikes().remove(userId);
        return filmStorage.removeLike(filmId, userId);
    }

    public Collection<Film> getPopularFilms(Integer count) {
        return filmStorage.getPopularFilms(count);
    }

    public Film getFilm(Integer id) {
        Map<Integer, Film> films = filmStorage.getAllFilms();
        if (!films.containsKey(id)) {
            throw new ElementNotFoundException("фильм " + id);
        }
        return filmStorage.getFilm(id);
    }

    public Collection<Film> getRecommendations(Integer userId) {
        return filmStorage.getRecommendations(userId);
    }

    public Collection<Film> getFilmsByDirector(Integer directorId, String sortBy, Integer from, Integer count) {
        directorService.findDirectorById(directorId);
        if (sortBy.equals("likes")) {
            return filmStorage.getFilmsByDirectorByLikes(directorId)
                    .stream()
                    .skip(from)
                    .limit(count)
                    .collect(Collectors.toList());
        }
        return filmStorage.getFilmsByDirectorByYear(directorId)
                .stream()
                .skip(from)
                .limit(count)
                .collect(Collectors.toList());
    }
}
