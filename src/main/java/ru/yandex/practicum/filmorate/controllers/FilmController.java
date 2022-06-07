package ru.yandex.practicum.filmorate.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.IncorrectParameterException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;

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
    public Collection<Film> getPopularFilms(@RequestParam(defaultValue = "10") Integer count) {
        return filmService.getPopularFilms(count);
    }

    @GetMapping("/{id}")
    public Film getFilm(@PathVariable Integer id) {
        return filmService.getFilm(id);
    }

    @GetMapping("/common")
    public Collection<Film> getCommonFilms(@RequestParam Integer userId,
                                           @RequestParam Integer friendId,
                                           @RequestParam(defaultValue = "desc",
                                                   required = false) String sort,
                                           @RequestParam(defaultValue = "10",
                                                   required = false) Integer count) {
        if (count <= 0) {
            throw new IncorrectParameterException("count");
        }
        return filmService.getCommonFilms(userId, friendId, sort, count);
    }
}
