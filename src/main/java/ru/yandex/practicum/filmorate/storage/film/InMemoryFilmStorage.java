package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ElementNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();
    private int id = 1;


    public Map<Integer, Film> getAllFilms() {
        return films;
    }

    public Film addFilm(Film film) {
        film.setId(id);
        films.put(id, film);
        id++;
        log.debug("Добавлен фильм {}.", film);
        return film;
    }

    public Film updateFilm(Film film) {
        if (!films.containsKey(film.getId())) {
            throw new ElementNotFoundException("фильм " + film.getId());
        }
        Film updateFilm = films.get(film.getId());
        updateFilm.setName(film.getName());
        updateFilm.setDescription(film.getDescription());
        updateFilm.setReleaseDate(film.getReleaseDate());
        updateFilm.setDuration(film.getDuration());
        films.put(film.getId(), updateFilm);
        log.debug("Обновлен фильм {}.", updateFilm.getId());
        return updateFilm;
    }

    public Integer removeFilm(Integer id) {
        films.remove(id);
        log.debug("Удален фильм {}.", id);
        return id;
    }

    public Film getFilm(Integer id) {
        if (!films.containsKey(id)) {
            throw new ElementNotFoundException("фильм" + id);
        }
        return films.get(id);
    }

    @Override
    public Collection<Film> getPopularFilms(Integer genreId, Integer year) {
        return null;
    }

    @Override
    public Collection<Film> getFilmsBySearch(String query, String by) {
        return null;
    }

    public Integer addLike(Integer filmId, Integer userId) {
        films.get(filmId).getLikes().add(userId);
        return userId;
    }

    public Integer removeLike(Integer filmId, Integer userId) {
        films.get(filmId).getLikes().remove(userId);
        return userId;
    }

    @Override
    public Collection<Film> getPopularFilms() {
        return null;
    }

    public Collection<Film> getRecommendations(Integer userId, Integer from, Integer size) {
        return List.of();
    }

    @Override
    public Collection<Film> getFilmsByDirectorByYear(Integer directorId) {
        return null;
    }

    @Override
    public Collection<Film> getFilmsByDirectorByLikes(Integer directorId) {
        return null;
    }

    @Override
    public Collection<Film> getCommonFilms(Integer userId, Integer friendId, Integer count) {
        return null;
    }
}
