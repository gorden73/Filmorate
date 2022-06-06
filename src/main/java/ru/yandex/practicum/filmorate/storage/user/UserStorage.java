package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Map;

public interface UserStorage {
    Map<Integer, User> allUsers();

    User add(User user);

    User update(User user);

    Integer remove(Integer id);

    User getUser(Integer id);

    Collection<User> getMutualFriends(Integer id, Integer id1);

    Collection<User> getUserFriends(Integer id);

    Integer removeFromFriends(Integer id, Integer removeFromId);

    User addToFriends(Integer id, Integer friendId);
}
