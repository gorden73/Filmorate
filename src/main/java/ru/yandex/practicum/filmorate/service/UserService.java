package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ElementNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Map;

@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;
    private final FilmService filmService;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage,
                       FilmService filmService) {
        this.userStorage = userStorage;
        this.filmService = filmService;
    }

    private boolean checkValidData(User user) {
        if (user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.error("Введен пустой email или отсутствует символ @.", UserService.class);
            throw new ValidationException("Email не может быть пустым и должен содержать символ @.");
        }
        if (user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.error("Введен пустой логин или логин содержит пробелы.", UserService.class);
            throw new ValidationException("Логин не может быть пустым или содержать пробелы.");
        }
        if (user.getName().isBlank() || user.getName() == null) {
            user.setName(user.getLogin());
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Введена дата рождения из будущего.", UserService.class);
            throw new ValidationException("Дата рождения не может быть в будущем.");
        }
        return true;
    }

    private boolean checkUpdateValidData(User user) {
        if (checkValidData(user)) {
            if (!allUsers().containsKey(user.getId())) {
                log.error("Введен неверный id.", UserService.class);
                throw new ElementNotFoundException("пользователь с id" + user.getId());
            }
        }
        return true;
    }

    public Map<Integer, User> allUsers() {
        return userStorage.getAllUsers();
    }

    public User addUser(User user) {
        if (checkValidData(user)) {
            return userStorage.addUser(user);
        }
        return user;
    }

    public User updateUser(User user) {
        if (checkUpdateValidData(user)) {
            return userStorage.updateUser(user);
        }
        return user;
    }

    public Integer removeUser(Integer id) {
        return userStorage.removeUser(id);
    }

    public User addToFriends(Integer id, Integer friendId) {
        Map<Integer, User> users = userStorage.getAllUsers();
        if (!users.containsKey(id)) {
            log.error("Не найден пользователь {}.", id);
            throw new ElementNotFoundException("пользователь " + id);
        }
        if (!users.containsKey(friendId)) {
            log.error("Не найден пользователь {}.", friendId);
            throw new ElementNotFoundException("пользователь " + friendId);
        }
        return userStorage.addToFriends(id, friendId);
    }

    public Integer removeFromFriends(Integer id, Integer removeFromId) {
        Map<Integer, User> users = userStorage.getAllUsers();
        if (!users.containsKey(id)) {
            log.error("Не найден пользователь {}.", id);
            throw new ElementNotFoundException("пользователь " + id);
        }
        if (!users.containsKey(removeFromId)) {
            log.error("Не найден пользователь {}.", removeFromId);
            throw new ElementNotFoundException("пользователь " + removeFromId);
        }
        return userStorage.removeFromFriends(id, removeFromId);
    }

    public Collection<User> getUserFriends(Integer id) {
        Map<Integer, User> users = userStorage.getAllUsers();
        if (!users.containsKey(id)) {
            throw new ElementNotFoundException("пользователь " + id);
        }
        return userStorage.getUserFriends(id);
    }

    public Collection<User> getMutualFriends(Integer id, Integer id1) {
        Map<Integer, User> users = userStorage.getAllUsers();
        if (!users.containsKey(id)) {
            log.error("Не найден пользователь {}.", id);
            throw new ElementNotFoundException("пользователь " + id);
        }
        if (!users.containsKey(id1)) {
            log.error("Не найден пользователь {}.", id1);
            throw new ElementNotFoundException("пользователь " + id1);
        }
        return userStorage.getMutualFriends(id, id1);
    }

    public User getUser(Integer id) {
        Map<Integer, User> users = userStorage.getAllUsers();
        if (!users.containsKey(id)) {
            log.error("Не найден пользователь {}.", id);
            throw new ElementNotFoundException("пользователь " + id);
        }
        return userStorage.getUser(id);
    }

    public Collection<Film> getRecommendations(Integer userId, Integer from, Integer size) {
        if(userStorage.getUser(userId) != null) {
            return filmService.getRecommendations(userId, from, size);
        } else {
            log.error("Не найден пользователь {}.", userId);
            throw new ElementNotFoundException(String.format("пользователь %d", userId));
        }
    }
}
