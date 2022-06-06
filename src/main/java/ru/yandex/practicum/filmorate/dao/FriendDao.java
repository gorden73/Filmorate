package ru.yandex.practicum.filmorate.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.ElementNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

@Repository
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

    public Collection<User> getUserFriends(Integer id) {
        String sqlGetUserFriends = "SELECT friend_id FROM friends WHERE user_id = ?";
        return jdbcTemplate.query(sqlGetUserFriends, (rs, rowNum) -> makeUser(rs), id);
    }

    public Collection<User> getMutualFriends(Integer id, Integer id1) {
        String sqlGetUserFriends = "SELECT friend_id FROM friends WHERE user_id = ? AND user_id = ? GROUP BY friend_id";
        return jdbcTemplate.query(sqlGetUserFriends, (rs, rowNum) -> makeUser(rs), id, id1);
    }

    private User makeUser(ResultSet rs) throws SQLException {
        int id = rs.getInt("user_id");
        String email = rs.getString("email");
        String login = rs.getString("login");
        String name = rs.getString("name");
        LocalDate birthday = rs.getDate("birthday").toLocalDate();
        String sqlFriends = "SELECT friend_id FROM friends WHERE user_id = ?";
        HashSet<Integer> friends = new HashSet<>(jdbcTemplate.query(sqlFriends,
                (rs1, rowNum) -> (rs1.getInt("friend_id")), id));
        HashMap<Integer, Boolean> friendStatus = new HashMap<>();
        String sqlFriendStatus = "SELECT * FROM friends WHERE user_id = ?";
        SqlRowSet friendsRows = jdbcTemplate.queryForRowSet(sqlFriendStatus, id);
        if (friendsRows.next()) {
            friendStatus.put(friendsRows.getInt("friend_id"), friendsRows.getBoolean("status"));
        }
        String sqlLikedFilms = "SELECT film_id FROM likes WHERE user_id = ?";
        HashSet<Integer> likedFilms = new HashSet<>(jdbcTemplate.query(sqlLikedFilms,
                (rs3, rowNum) -> (rs3.getInt("film_id")), id));
        return new User(id, email, login, name, birthday, friends, friendStatus, likedFilms);
    }
}
