package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ElementNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();
    private int id = 1;


    public Map<Integer, User> getAllUsers() {
        return users;
    }

    public User addUser(User user) {
        user.setId(id);
        users.put(id, user);
        id++;
        log.debug("Добавлен пользователь {}.", user);
        return user;
    }

    public User updateUser(User user) {
        if (!users.containsKey(user.getId())) {
            throw new ElementNotFoundException("пользователь " + user.getId());
        }
        User updateUser = users.get(user.getId());
        updateUser.setEmail(user.getEmail());
        updateUser.setLogin(user.getLogin());
        updateUser.setName(user.getName());
        updateUser.setBirthday(user.getBirthday());
        users.put(user.getId(), updateUser);
        log.debug("Обновлены данные пользователя {}.", updateUser.getId());
        return updateUser;
    }

    public Integer removeUser(Integer id) {
        users.remove(id);
        log.debug("Удален пользователь {}", id);
        return id;
    }

    public User addToFriends(Integer id, Integer friendId) {
        users.get(id).getFriends().add(friendId);
        users.get(friendId).getFriends().add(id);
        return users.get(friendId);
    }

    public Integer removeFromFriends(Integer id, Integer removeFromId) {
        users.get(id).getFriends().remove(removeFromId);
        users.get(removeFromId).getFriends().remove(id);
        return id;
    }

    public Collection<User> getUserFriends(Integer id) {
        List<User> friends = new ArrayList<>();
        Set<Integer> userSet = users.get(id).getFriends();
        for (Integer user : userSet) {
            friends.add(users.get(user));
        }
        return friends;
    }

    public Collection<User> getMutualFriends(Integer id, Integer id1) {
        List<User> friendsNames = new ArrayList<>();
        Set<Integer> userSet = users.get(id).getFriends();
        Set<Integer> userSet1 = users.get(id1).getFriends();
        for (Integer user : userSet) {
            if (userSet1.contains(user)) {
                friendsNames.add(users.get(user));
            }
        }
        return friendsNames;
    }

    public User getUser(Integer id) {
        return users.get(id);
    }

    public Collection<Film> getRecommendations(Integer userId, Integer from, Integer size) {
        return null;
    }
}
