package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface FriendDao {
    User addToFriends(Integer id, Integer friendId);

    Integer removeFromFriends(Integer id, Integer friendId);

    Collection<User> getUserFriends(Integer id);

    Collection<User> getMutualFriends(Integer id, Integer id1);
}
