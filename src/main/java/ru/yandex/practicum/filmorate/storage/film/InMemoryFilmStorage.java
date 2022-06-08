package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ElementNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

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

    public Collection<Film> getPopularFilms(Integer count) {
        if (count > 0 && count < films.size()) {
            return films.values().stream()
                    .sorted((f1, f2) -> Integer.compare(f2.getLikes().size(), f1.getLikes().size()))
                    .limit(count).collect(Collectors.toList());
        }
        return films.values().stream()
                .sorted((f1, f2) -> Integer.compare(f2.getLikes().size(), f1.getLikes().size()))
                .limit(films.size()).collect(Collectors.toList());
    }

    public Integer addLike(Integer filmId, Integer userId) {
        films.get(filmId).getLikes().add(userId);
        return userId;
    }

    public Integer removeLike(Integer filmId, Integer userId) {
        films.get(filmId).getLikes().remove(userId);
        return userId;
    }

    public Collection<Film> getRecommendations(Integer userId) {
        return films.values(); // НАДО ДОРАБОТАТЬ
    }
}
