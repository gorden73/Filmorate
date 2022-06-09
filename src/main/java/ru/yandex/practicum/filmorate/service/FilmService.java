package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ElementNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.IncorrectParameterException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final DirectorService directorService;
    private static final LocalDate MOVIE_BIRTHDAY = LocalDate.of(1895, 12, 28);
    private final UserService userService;


    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage,
                       DirectorService directorService, UserService userService) {
        this.filmStorage = filmStorage;
        this.directorService = directorService;
        this.userService = userService;
    }

    private boolean checkValidData(Film film) {
        if (film.getDescription().length() > 200) {
            log.error("Описание больше 200 символов.", FilmService.class);
            throw new ValidationException("Описание не должно составлять больше 200 символов.");
        }
        if (film.getDescription().isBlank() || film.getDescription().isEmpty()) {
            log.error("Описание пустое или состоит из пробелов.");
            throw new ValidationException("Описание не должно быть пустым или состоять из " +
                    "пробелов.");
        }
        if (film.getReleaseDate().isBefore(MOVIE_BIRTHDAY)) {
            log.error("Дата выхода фильма в прокат не может быть раньше 28.12.1895.",
                    FilmService.class);
            throw new ValidationException("Дата выхода фильма в прокат не может быть раньше " +
                    "28.12.1895.");
        }
        if (film.getDuration() <= 0) {
            log.error("Продолжительность фильма отрицательное число или равно нулю.",
                    FilmService.class);
            throw new ValidationException("Продолжительность фильма должна быть больше нуля.");
        }
        return true;
    }

    private boolean checkAddValidData(Film film) {
        if (checkValidData(film)) {
            if (film.getName().isBlank()) {
                log.error("Название пустое.", FilmService.class);
                throw new ValidationException("Название не может быть пустым.");
            }
        }
        return true;
    }

    private boolean checkUpdateValidData(Film film) {
        if (checkValidData(film)) {
            if (film.getId() == null) {
                log.error("Не введен id.", FilmService.class);
                throw new ValidationException("Нужно задать id.");
            }
            if (!getAllFilms().containsKey(film.getId())) {
                log.error("Неверный id.", FilmService.class);
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
            final Film createdFilm = filmStorage.addFilm(film);
            Optional<Director> optionalDirector = Optional.empty();
            if (film.getDirector() != null) {
                optionalDirector = film.getDirector().stream().findAny();
            }
            Optional<Director> optFoundedDirector;
            if (optionalDirector.isPresent()) {
                optFoundedDirector = directorService.findDirectorById(optionalDirector
                        .get().getId());
                optFoundedDirector.ifPresent(director ->
                        directorService.addFilmDirector(director.getId(), createdFilm.getId()));
            }
            return filmStorage.getFilm(createdFilm.getId());
        }
        return film;
    }

    public Film updateFilm(Film film) {
        if (checkUpdateValidData(film) && checkAddValidData(film)) {
            final Film updatedFilm = filmStorage.updateFilm(film);
            Optional<Director> optionalDirector = Optional.empty();
            if (film.getDirector() != null) {
                optionalDirector = film.getDirector().stream().findAny();
            }
            Optional<Director> optFoundedDirector;
            if (optionalDirector.isPresent()) {
                optFoundedDirector = directorService.findDirectorById(optionalDirector
                        .get().getId());
                optFoundedDirector.ifPresent(director ->
                        directorService.addFilmDirector(director.getId(), updatedFilm.getId()));
            }
            return updatedFilm;
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

    public Collection<Film> getPopularFilms(Integer genreId, Integer year,
                                             Integer count, Integer from) {
        return filmStorage.getPopularFilms(genreId, year)
                .stream()
                .skip(from)
                .limit(count)
                .collect(Collectors.toList());
    }

    public Film getFilm(Integer id) {
        Map<Integer, Film> films = filmStorage.getAllFilms();
        if (!films.containsKey(id)) {
            throw new ElementNotFoundException("фильм " + id);
        }
        return filmStorage.getFilm(id);
    }

    public Collection<Film> getRecommendations(Integer userId, Integer from, Integer size) {
        return filmStorage.getRecommendations(userId, from, size);
    }

    public Collection<Film> getFilmsBySearch(String query, String by)
            throws IncorrectParameterException {
        String queryResult = query.toLowerCase().trim();
        String byResult = by.toLowerCase().trim();
        return filmStorage.getFilmsBySearch(queryResult, byResult);
    }

    public Collection<Film> getFilmsByDirector(Integer directorId, String sortBy,
                                               Integer from, Integer count) {
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

    public Collection<Film> getCommonFilms(Integer userId, Integer friendId,
                                           Integer count, Integer from) {
        final Optional<User> optionalUser = userService.findUserById(userId);
        final Optional<User> optionalFriend = userService.findUserById(friendId);
        if (optionalUser.isPresent() && optionalFriend.isPresent()) {
            return filmStorage.getCommonFilms(userId, friendId, count)
                    .stream()
                    .skip(from)
                    .limit(count)
                    .collect(Collectors.toList());
        }
        return List.of();
    }
}

