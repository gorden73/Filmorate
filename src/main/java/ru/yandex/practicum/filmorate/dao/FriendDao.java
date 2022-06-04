package ru.yandex.practicum.filmorate.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ElementNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

@Component
@Slf4j
public class FriendDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FriendDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public User addToFriends(Integer id, Integer friendId) {
        String sqlAddToFriends = "INSERT INTO friends(user_id, friend_id) VALUES (?, ?)";
        jdbcTemplate.update(sqlAddToFriends, id, friendId);
        if (checkFriend(id, friendId)) {
            String sqlChangeStatus1 = "UPDATE friends SET status = ? WHERE user_id = ? AND friend_id = ?";
            jdbcTemplate.update(sqlChangeStatus1, true, id, friendId);
            String sqlChangeStatus2 = "UPDATE friends SET status = ? WHERE user_id = ? AND friend_id = ?";
            jdbcTemplate.update(sqlChangeStatus2, true, friendId, id);
        }
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from users where user_id = ?", friendId);
        if (userRows.next()) {
            log.debug("Пользователь {} отправил заявку в друзья пользователю {}.", id, friendId);
            return new User(userRows.getInt("user_id"), userRows.getString("email"),
                    userRows.getString("login"), userRows.getString("name"),
                    userRows.getDate("birthday").toLocalDate());
        }
        return null;
    }

    public Integer removeFromFriends(Integer id, Integer friendId) {
        if (checkFriend(id, friendId)) {
            throw new ElementNotFoundException("пользователь " + id);
        }
        if (!checkFriend(friendId, id)) {
            throw new ElementNotFoundException("пользователь " + friendId);
        }
        String sqlRemoveLike = "DELETE FROM friends WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(sqlRemoveLike, id, friendId);
        if (checkFriend(id, friendId)) {
            String sqlChangeStatus = "UPDATE friends SET status = ? WHERE user_id = ? AND friend_id = ?";
            jdbcTemplate.update(sqlChangeStatus, false, friendId, id);
        }
        log.debug("Пользователь {} удалил из друзей пользователя {}.", id, friendId);
        return friendId;
    }

    private boolean checkFriend(Integer id, Integer friendId) {
        String sqlCheckFriend = "SELECT friend_id FROM friends WHERE user_id = ? AND friend_id = ?";
        List<Integer> friends = jdbcTemplate.query(sqlCheckFriend, (rs, rowNum) -> rs.getInt("friend_id"),
                friendId, id);
        if (friends.contains(id)) {
            return true;
        }
        return false;
    }
}
