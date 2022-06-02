package ru.yandex.practicum.filmorate.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

@Component
public class FriendDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FriendDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public User addToFriends(Integer id, Integer friendId) {
        String sqlAddToFriends = "INSERT INTO friends(user_id, friend_id) VALUES (?, ?)";
        jdbcTemplate.update(sqlAddToFriends, id, friendId);
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from users where user_id = ?", friendId);
        if (userRows.next()) {
            return new User(userRows.getInt("user_id"), userRows.getString("email"),
                    userRows.getString("login"), userRows.getString("name"),
                    userRows.getDate("birthday").toLocalDate());
        }
        return null;
    }

//    private boolean checkFriend(Integer id, Integer friendId) {
//        String sqlCheckFriend = "SELECT * FROM friends WHERE user_id = ? AND friend_id = ?";
//        jdbcTemplate.
//        return true;
//    }
}
