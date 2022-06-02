package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ElementNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();
    private int id = 1;


    public Map<Integer, Film> allFilms() {
        return films;
    }

    public Film add(Film film) {
        film.setId(id);
        films.put(id, film);
        id++;
        log.debug("Добавлен фильм {}.", film);
        return film;
    }

    public Film update(Film film) {
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

    public Integer remove(Integer id) {
        films.remove(id);
        log.debug("Удален фильм {}.", id);
        return id;
    }
}
