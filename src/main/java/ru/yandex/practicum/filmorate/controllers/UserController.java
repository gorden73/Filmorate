package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.Collection;

@RequestMapping("/users")
@RestController
@Slf4j
public class UserController {
    private final UserService userService;
    private final FilmService filmService;

    @Autowired
    public UserController(UserService userService, FilmService filmService) {
        this.userService = userService;
        this.filmService = filmService;
    }

    @GetMapping
    public Collection<User> getAllUsers() {
        return userService.allUsers().values();
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable Integer id) {
        return userService.findUserById(id);
    }

    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        return userService.addUser(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        return userService.updateUser(user);
    }

    @DeleteMapping("/{id}")
    public Integer removeUser(@PathVariable Integer id) {
        return userService.removeUser(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public User addToFriends(@PathVariable Integer id, @PathVariable Integer friendId) {
        return userService.addToFriends(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public Integer removeFromFriends(@PathVariable Integer id, @PathVariable Integer friendId) {
        return userService.removeFromFriends(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public Collection<User> getUserFriends(@PathVariable Integer id) {
        return userService.getUserFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> getMutualFriends(@PathVariable Integer id,
                                             @PathVariable Integer otherId) {
        return userService.getMutualFriends(id, otherId);
    }

    @GetMapping("/{id}/recommendations")
    public Collection<Film> getRecommendations(@PathVariable Integer id,
                                               @RequestParam(value = "page", defaultValue = "0")
                                               Integer page, @RequestParam(value = "size",
            defaultValue = "10") Integer size) {
        if (page < 0) {
            throw new IllegalArgumentException(String.format("Некорректный ввод номера страницы " +
                    "%d.", page));
        }
        if (size <= 0) {
            throw new IllegalArgumentException(String.format("Некорректный ввод количества " +
                    "результатов %d.", size));
        }
        Integer from = page * size;
        userService.findUserById(id);
        return filmService.getRecommendations(id, from, size);
    }
}
