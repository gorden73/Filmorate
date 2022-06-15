package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public interface UserStorage {
    Map<Integer, User> getAllUsers();

    User addUser(User user);

    User updateUser(User user);

    Integer removeUser(Integer id);

    Optional<User> getUserById(Integer id);

    Collection<User> getMutualFriends(Integer id, Integer id1);

    Collection<User> getUserFriends(Integer id);

    Integer removeFromFriends(Integer id, Integer removeFromId);

    User addToFriends(Integer id, Integer friendId);
}
