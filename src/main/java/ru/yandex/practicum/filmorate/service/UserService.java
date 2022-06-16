package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FeedDao;
import ru.yandex.practicum.filmorate.exceptions.ElementNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;
    private FeedDao feedDbStorage;

    public UserService(@Qualifier("userDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage,
                       FeedDao feedDbStorage) {
        this.userStorage = userStorage;
        this.feedDbStorage = feedDbStorage;
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
        if (userStorage.getUserById(id).isEmpty()) {
            log.error("Не найден пользователь {}.", id);
            throw new ElementNotFoundException("пользователь " + id);
        }
        if (userStorage.getUserById(friendId).isEmpty()) {
            log.error("Не найден пользователь {}.", friendId);
            throw new ElementNotFoundException("пользователь " + friendId);
        }
        if (userStorage.getUserById(id).get().getFriends().contains(friendId)) {
            throw new ValidationException("пользователь уже добавил " + friendId + " в друзья");
        }
        feedDbStorage.addFeed(new Feed(id, "FRIEND", "ADD", friendId));
        return userStorage.addToFriends(id, friendId);
    }

    public Integer removeFromFriends(Integer id, Integer removeFromId) {
        if (userStorage.getUserById(id).isEmpty()) {
            log.error("Не найден пользователь {}.", id);
            throw new ElementNotFoundException("пользователь " + id);
        }
        if (userStorage.getUserById(removeFromId).isEmpty()) {
            log.error("Не найден пользователь {}.", removeFromId);
            throw new ElementNotFoundException("пользователь " + removeFromId);
        }
        Integer remove = userStorage.removeFromFriends(id, removeFromId);
        feedDbStorage.addFeed(new Feed(id, "FRIEND", "REMOVE", removeFromId));
        return remove;
    }

    public Collection<User> getUserFriends(Integer id) {
        if (userStorage.getUserById(id).isEmpty()) {
            throw new ElementNotFoundException("пользователь " + id);
        }
        return userStorage.getUserFriends(id);
    }

    public Collection<User> getMutualFriends(Integer id, Integer id1) {
        if (userStorage.getUserById(id).isEmpty()) {
            log.error("Не найден пользователь {}.", id);
            throw new ElementNotFoundException("пользователь " + id);
        }
        if (userStorage.getUserById(id1).isEmpty()) {
            log.error("Не найден пользователь {}.", id1);
            throw new ElementNotFoundException("пользователь " + id1);
        }
        return userStorage.getMutualFriends(id, id1);
    }

    public User findUserById(Integer id) {
        final Optional<User> optionalUser = userStorage.getUserById(id);
        if (optionalUser.isEmpty()) {
            log.error("Не найден пользователь {}.", id);
            throw new ElementNotFoundException(String.format("Пользователь c ID %s не найден", id));
        }
        return optionalUser.get();
    }
}
