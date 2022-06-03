package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FriendDao;
import ru.yandex.practicum.filmorate.exceptions.ElementNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.*;

@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;
    private final FriendDao friendDao;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage, FriendDao friendDao) {
        this.userStorage = userStorage;
        this.friendDao = friendDao;
    }

    private boolean checkValidData(User user) {
        if (user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.error("Введен пустой email или отсутствует символ @.", InMemoryUserStorage.class);
            throw new ValidationException("Email не может быть пустым и должен содержать символ @.");
        }
        if (user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.error("Введен пустой логин или логин содержит пробелы.", InMemoryUserStorage.class);
            throw new ValidationException("Логин не может быть пустым или содержать пробелы.");
        }
        if (user.getName().isBlank() || user.getName() == null) {
            user.setName(user.getLogin());
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Введена дата рождения из будущего.", InMemoryUserStorage.class);
            throw new ValidationException("Дата рождения не может быть в будущем.");
        }
        return true;
    }

    private boolean checkUpdateValidData(User user) {
        if (checkValidData(user)) {
            if (!allUsers().containsKey(user.getId())) {
                log.error("Введен неверный id.", InMemoryUserStorage.class);
                throw new ElementNotFoundException("пользователь с id" + user.getId());
            }
        }
        return true;
    }

    public Map<Integer, User> allUsers() {
        return userStorage.allUsers();
    }

    public User add(User user) {
        if (checkValidData(user)) {
            return userStorage.add(user);
        }
        return user;
    }

    public User update(User user) {
        if (checkUpdateValidData(user)) {
            return userStorage.update(user);
        }
        return user;
    }

    public User addToFriends(Integer id, Integer friendId) {
        Map<Integer, User> userMap = userStorage.allUsers();
        if (!userStorage.allUsers().containsKey(id)) {
            throw new ElementNotFoundException("пользователь" + id);
        }
        if (!userStorage.allUsers().containsKey(friendId)) {
            throw new ElementNotFoundException("пользователь" + friendId);
        }
        userMap.get(id).getFriends().add(friendId);
        userMap.get(friendId).getFriends().add(id);
        return friendDao.addToFriends(id, friendId); // нужно использовать FriendDao
    }

    public Integer removeFromFriends(Integer id, Integer removeFromId) {
        Map<Integer, User> userMap = userStorage.allUsers();
        if (!userStorage.allUsers().containsKey(id)) {
            throw new ElementNotFoundException("пользователь" + id);
        }
        if (!userStorage.allUsers().containsKey(removeFromId)) {
            throw new ElementNotFoundException("пользователь" + removeFromId);
        }
        userMap.get(id).getFriends().remove(removeFromId);
        userMap.get(removeFromId).getFriends().remove(id);
        return friendDao.removeFromFriends(id, removeFromId);
    }

    public Collection<User> getUserFriends(Integer id) {
        List<User> friends = new ArrayList<>();
        if (!userStorage.allUsers().containsKey(id)) {
            throw new ElementNotFoundException("пользователь " + id);
        }
        Set<Integer> userSet = userStorage.allUsers().get(id).getFriends();
        for (Integer user : userSet) {
            friends.add(userStorage.allUsers().get(user));
        }
        return friends;
    }

    public Collection<User> getMutualFriends(Integer id, Integer id1) {
        List<User> friendsNames = new ArrayList<>();
        if (!userStorage.allUsers().containsKey(id)) {
            throw new ElementNotFoundException("пользователь " + id);
        }
        if (!userStorage.allUsers().containsKey(id1)) {
            throw new ElementNotFoundException("пользователь " + id1);
        }
        Set<Integer> userSet = userStorage.allUsers().get(id).getFriends();
        Set<Integer> userSet1 = userStorage.allUsers().get(id1).getFriends();
        for (Integer user : userSet) {
            if (userSet1.contains(user)) {
                friendsNames.add(userStorage.allUsers().get(user));
            }
        }
        return friendsNames;
    }

    public User getUser(Integer id) {
        if (!userStorage.allUsers().containsKey(id)) {
            throw new ElementNotFoundException("пользователь " + id);
        }
        return userStorage.allUsers().get(id);
    }

    public Integer remove(Integer id) {
        return userStorage.remove(id);
    }
}
