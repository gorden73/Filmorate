package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ElementNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Map<Integer, Film> allFilms() {
        return filmStorage.allFilms();
    }

    public Film add(Film film) {
        return filmStorage.add(film);
    }

    public Film update(Film film) {
        return filmStorage.update(film);
    }

    public void remove(Integer id) {
        filmStorage.remove(id);
    }

    public void addLike(Integer filmId, Integer userId) {
        if (!filmStorage.allFilms().containsKey(filmId)) {
            throw new ElementNotFoundException("фильм" + filmId);
        }
        filmStorage.allFilms().get(filmId).getLikes().add(userId);
    }

    public void removeLike(Integer filmId, Integer userId) {
        if (!filmStorage.allFilms().containsKey(filmId)) {
            throw new ElementNotFoundException("фильм" + filmId);
        }
        if (!getFilm(filmId).getLikes().contains(userId)) {
            throw new ElementNotFoundException("лайк пользователя " + userId);
        }
        filmStorage.allFilms().get(filmId).getLikes().remove(userId);
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
