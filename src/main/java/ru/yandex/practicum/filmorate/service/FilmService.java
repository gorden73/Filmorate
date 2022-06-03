package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.LikesDao;
import ru.yandex.practicum.filmorate.exceptions.ElementNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final LikesDao likesDao;
    private final LocalDate movieBirthday = LocalDate.of(1895, 12, 28);


    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage, LikesDao likesDao) {
        this.filmStorage = filmStorage;
        this.likesDao = likesDao;
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
        if (film.getReleaseDate().isBefore(movieBirthday)) {
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
            if (!allFilms().containsKey(film.getId())) {
                log.error("Неверный id.", InMemoryFilmStorage.class);
                throw new ElementNotFoundException("фильм с id" + film.getId());
            }
        }
        return true;
    }

    public Map<Integer, Film> allFilms() {
        return filmStorage.allFilms();
    }

    public Film add(Film film) {
        if (checkAddValidData(film)) {
            return filmStorage.add(film);
        }
        return film;
    }

    public Film update(Film film) {
        if (checkUpdateValidData(film) && checkAddValidData(film)) {
            return filmStorage.update(film);
        }
        return film;
    }

    public Integer remove(Integer id) {
        return filmStorage.remove(id);
    }

    public Integer addLike(Integer filmId, Integer userId) {
        if (!filmStorage.allFilms().containsKey(filmId)) {
            throw new ElementNotFoundException("фильм" + filmId);
        }
        filmStorage.allFilms().get(filmId).getLikes().add(userId);
        return likesDao.addLike(filmId, userId);// надо использовать likesDao
    }

    public Integer removeLike(Integer filmId, Integer userId) {
        if (!filmStorage.allFilms().containsKey(filmId)) {
            throw new ElementNotFoundException("фильм" + filmId);
        }
        if (!getFilm(filmId).getLikes().contains(userId)) {
            throw new ElementNotFoundException("лайк пользователя " + userId);
        }
        filmStorage.allFilms().get(filmId).getLikes().remove(userId);
        return likesDao.removeLike(filmId, userId);
    }

    public Collection<Film> getTopTenFilms(Integer count) {
        if (count > 0 && count < filmStorage.allFilms().size()) {
            return filmStorage.allFilms().values().stream()
                    .sorted((f1, f2) -> Integer.compare(f2.getLikes().size(), f1.getLikes().size()))
                    .limit(count).collect(Collectors.toList());
        }
        return filmStorage.allFilms().values().stream()
                .sorted((f1, f2) -> Integer.compare(f2.getLikes().size(), f1.getLikes().size()))
                .limit(filmStorage.allFilms().size()).collect(Collectors.toList());
    }

    public Film getFilm(Integer id) {
        if (!filmStorage.allFilms().containsKey(id)) {
            throw new ElementNotFoundException("фильм" + id);
        }
        return filmStorage.allFilms().get(id);
    }
}
