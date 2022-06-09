package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.constraints.Min;
import java.util.Collection;

@Slf4j
@RequestMapping("/films")
@RestController
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public Collection<Film> getAllFilms() {
        return filmService.getAllFilms().values();
    }

    @PostMapping
    public Film addFilm(@RequestBody Film film) {
        return filmService.addFilm(film);
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        return filmService.updateFilm(film);
    }

    @DeleteMapping("/{id}")
    public Integer removeFilm(@PathVariable Integer id) {
        return filmService.removeFilm(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public Integer addLike(@PathVariable Integer id, @PathVariable Integer userId) {
        return filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Integer deleteLike(@PathVariable Integer id, @PathVariable Integer userId) {
        return filmService.removeLike(id, userId);
    }

    @GetMapping("/popular")
    public Collection<Film> getPopularFilms(@RequestParam(defaultValue = "10") Integer count,
                                            @RequestParam(defaultValue = "0") Integer page,
                                            @RequestParam(required = false) Integer genreId,
                                            @RequestParam(required = false) Integer year) {
        if (count <= 0) {
            throw new IllegalArgumentException("count");
        }
        if (page < 0) {
            throw new IllegalArgumentException("page");
        }
        Integer from = page * count;
        return filmService.getPopularFilms(genreId, year, count, from);
    }

    @GetMapping("/{id}")
    public Film getFilm(@PathVariable Integer id) {
        return filmService.getFilm(id);
    }

    @GetMapping("/director/{directorId}")
    public Collection<Film> getFilmsByDirector(@PathVariable Integer directorId,
                                               @RequestParam(defaultValue = "year") String sortBy,
                                               @RequestParam(defaultValue = "10") Integer count,
                                               @RequestParam(defaultValue = "0") Integer page) {
        if (count <= 0) {
            throw new IllegalArgumentException("count");
        }
        if (page < 0) {
            throw new IllegalArgumentException("page");
        }
        if (!(sortBy.equals("year") || sortBy.equals("likes"))) {
            throw new IllegalArgumentException("sortBy");
        }
        Integer from = count * page;
        return filmService.getFilmsByDirector(directorId, sortBy, from, count);
    }

    @GetMapping("/common")
    public Collection<Film> getCommonFilms(@RequestParam Integer userId,
                                           @RequestParam Integer friendId,
                                           @RequestParam(defaultValue = "0") Integer page,
                                           @RequestParam(defaultValue = "10") Integer count) {
        if (page < 0) {
            throw new IllegalArgumentException("page");
        }
        if (count <= 0) {
            throw new IllegalArgumentException("count");
        }
        Integer from = count * page;
        return filmService.getCommonFilms(userId, friendId, count, from);
    }

    @GetMapping("/search")
    public Collection<Film> getFilmsBySearch(
            @RequestParam (defaultValue = "") String query,
            @RequestParam (defaultValue = "") String by) {
        return filmService.getFilmsBySearch(query, by);
    }
}
