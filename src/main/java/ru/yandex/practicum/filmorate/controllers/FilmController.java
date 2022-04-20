package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.*;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RequestMapping("/films")
@RestController
@Slf4j
public class FilmController {
    Map<Integer, Film> films = new HashMap<>();
    int id = 1;

    @GetMapping
    public Collection<Film> allFilms(){
        return films.values();
    }

    @PostMapping
    public Film add(@RequestBody Film film) {
        if (film.getId() != null) {
            log.error("Введен id.", FilmController.class);
            throw new ValidationException("Id генерируется автоматически.");
        }
        if (film.getName().isBlank()) {
            log.error("Название пустое.", FilmController.class);
            throw new ValidationException("Название не может быть пустым.");
        }
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
        film.setId(id);
        films.put(id, film);
        id++;
        log.debug("Добавлен фильм " + film);
        return film;
    }

    @PutMapping
    public void update(@RequestBody Film film) {
        if (film.getId() == null) {
            log.error("Не введен id.", FilmController.class);
            throw new ValidationException("Нужно задать id.");
        }
        if (!films.containsKey(film.getId())) {
            log.error("Неверный id.", FilmController.class);
            throw new ValidationException("Фильма с id" + film.getId() + " нет.");
        }
        if (film.getDescription().length() > 200) {
            log.error("Описание больше 200 символов.", FilmController.class);
            throw new ValidationException("Описание не должно составлять больше 200 символов.");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.error("Дата выхода фильма в прокат некорректна.", FilmController.class);
            throw new ValidationException("Дата выхода фильма в прокат некорректна.");
        }
        if (film.getDuration().isNegative() || film.getDuration().isZero()) {
            log.error("Продолжительность фильма отрицательное число или равно нулю.", FilmController.class);
            throw new ValidationException("Продолжительность фильма должна быть больше нуля.");
        }
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
