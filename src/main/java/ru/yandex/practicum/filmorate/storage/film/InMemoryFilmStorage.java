package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.controllers.FilmController;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private Map<Integer, Film> films = new HashMap<>();
    private int id = 1;

    private boolean checkValidData(Film film) {
        if (film.getDescription().length() > 200) {
            log.error("Описание больше 200 символов.", FilmController.class);
            throw new ValidationException("Описание не должно составлять больше 200 символов.");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.error("Дата выхода фильма в прокат не может быть раньше 28.12.1895.", FilmController.class);
            throw new ValidationException("Дата выхода фильма в прокат не может быть раньше 28.12.1895.");
        }
        if (film.getDuration().isNegative() || film.getDuration().isZero()) {
            log.error("Продолжительность фильма отрицательное число или равно нулю.", FilmController.class);
            throw new ValidationException("Продолжительность фильма должна быть больше нуля.");
        }
        return true;
    }

    private boolean checkAddValidData(Film film) {
        if (checkValidData(film)) {
            if (film.getName().isBlank()) {
                log.error("Название пустое.", FilmController.class);
                throw new ValidationException("Название не может быть пустым.");
            }
        }
        return true;
    }

    private boolean checkUpdateValidData(Film film) {
        if (checkValidData(film)) {
            if (film.getId() == null) {
                log.error("Не введен id.", FilmController.class);
                throw new ValidationException("Нужно задать id.");
            }
            if (!films.containsKey(film.getId())) {
                log.error("Неверный id.", FilmController.class);
                throw new ValidationException("Фильма с id" + film.getId() + " нет.");
            }
        }
        return true;
    }

    public Collection<Film> allFilms() {
        return films.values();
    }

    public void add(Film film) {
        if (checkAddValidData(film)) {
            film.setId(id);
            films.put(id, film);
            id++;
            log.debug("Добавлен фильм " + film);
        }
    }

    public void update(Film film) {
        if (checkUpdateValidData(film)) {
            Film updateFilm = films.get(film.getId());
            if (!film.getName().isBlank()) {
                updateFilm.setName(film.getName());
            }
            updateFilm.setDescription(film.getDescription());
            updateFilm.setReleaseDate(film.getReleaseDate());
            updateFilm.setDuration(film.getDuration());
            films.put(film.getId(), updateFilm);
            log.debug("Обновлен фильм " + updateFilm);
        }
    }

    public void remove(Integer id) {
        films.remove(id);
    }
}
